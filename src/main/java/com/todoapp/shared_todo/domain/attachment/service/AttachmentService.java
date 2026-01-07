package com.todoapp.shared_todo.domain.attachment.service;

import com.todoapp.shared_todo.domain.attachment.dto.*;
import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.attachment.port.S3ServicePort;
import com.todoapp.shared_todo.domain.attachment.repository.AttachmentRepository;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;
    private final S3ServicePort s3ServicePort;

    @Value("${aws.s3.presigned-url-expiration:3600}")
    private Integer presignedUrlExpiration;

    @Value("${share.link.base-url}")
    private String shareLinkBaseUrl;

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "txt"
    );

    /**
     * 파일 업로드
     */
    @Transactional
    public AttachmentResponse uploadFile(Long boardId, Long userId, MultipartFile file) throws IOException {
        // 파일 검증
        validateFile(file);

        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // S3 Key 및 저장 파일명 생성
        String s3Key = s3ServicePort.generateS3Key(file.getOriginalFilename());
        String storeFilename = s3ServicePort.generateStoreFilename(file.getOriginalFilename());

        // S3에 파일 업로드
        s3ServicePort.uploadFile(
                file.getInputStream(),
                s3Key,
                file.getContentType(),
                file.getSize()
        );

        // Attachment 엔티티 생성 및 저장
        Attachment attachment = Attachment.create(
                board,
                user,
                file.getOriginalFilename(),
                storeFilename,
                s3Key,
                null, // s3Url은 필요시 설정
                file.getSize(),
                file.getContentType()
        );

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return toResponse(savedAttachment);
    }

    /**
     * 보드별 첨부파일 목록 조회
     */
    public AttachmentListResponse getAttachments(Long boardId, Long userId) {
        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);

        List<Attachment> attachments = attachmentRepository.findByBoardAndIsDeletedFalse(board);
        List<AttachmentResponse> attachmentResponses = attachments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return AttachmentListResponse.builder()
                .attachments(attachmentResponses)
                .totalCount((long) attachmentResponses.size())
                .build();
    }

    /**
     * 첨부파일 단건 조회
     */
    public AttachmentResponse getAttachment(Long boardId, Long attachmentId, Long userId) {
        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);

        Attachment attachment = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(attachmentId, board)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        return toResponse(attachment);
    }

    /**
     * Presigned URL 생성 (다운로드용)
     */
    public PresignedUrlResponse generatePresignedDownloadUrl(Long boardId, Long attachmentId, Long userId) {
        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);

        Attachment attachment = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(attachmentId, board)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        String presignedUrl = s3ServicePort.generatePresignedDownloadUrl(attachment.getS3Key());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(presignedUrlExpiration);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * AWS S3 공유 링크 생성
     * S3에 저장된 첨부파일을 인증 없이 공개적으로 접근할 수 있는 링크를 생성합니다.
     * 생성된 링크를 통해 Presigned URL을 발급받아 S3 파일을 다운로드할 수 있습니다.
     * 
     * @param boardId 보드 ID
     * @param attachmentId 첨부파일 ID
     * @param userId 사용자 ID (권한 확인용)
     * @return 공유 링크 정보 (UUID, URL)
     */
    @Transactional
    public AttachmentShareResponse createAwsShareLink(Long boardId, Long attachmentId, Long userId) {
        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);

        Attachment attachment = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(attachmentId, board)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        // AWS S3 공유 링크 생성
        attachment.createAwsShareLink();
        Attachment savedAttachment = attachmentRepository.save(attachment);

        String shareUrl = shareLinkBaseUrl + "/" + savedAttachment.getAwsShareUuid();

        return AttachmentShareResponse.builder()
                .shareUuid(savedAttachment.getAwsShareUuid())
                .shareUrl(shareUrl)
                .build();
    }

    /**
     * AWS S3 공유 링크로 첨부파일 조회 (인증 불필요)
     * 공유 링크 UUID를 통해 S3에 저장된 첨부파일의 메타데이터를 조회합니다.
     * 
     * @param awsShareUuid AWS S3 공유 링크 UUID
     * @return 첨부파일 정보
     * @throws IllegalArgumentException 유효하지 않은 공유 링크인 경우
     */
    public AttachmentResponse getAttachmentByAwsShareLink(String awsShareUuid) {
        Attachment attachment = attachmentRepository.findByAwsShareUuidAndValid(awsShareUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공유 링크입니다."));
        return toResponse(attachment);
    }

    /**
     * AWS S3 공유 링크로 Presigned URL 생성 (인증 불필요)
     * 공유 링크 UUID를 통해 S3 파일 다운로드를 위한 Presigned URL을 생성합니다.
     * Presigned URL은 제한된 시간 동안만 유효하며, 직접 S3에서 파일을 다운로드할 수 있습니다.
     * 
     * @param awsShareUuid AWS S3 공유 링크 UUID
     * @return Presigned URL 및 만료 시각
     * @throws IllegalArgumentException 유효하지 않은 공유 링크인 경우
     */
    public PresignedUrlResponse generateAwsPresignedDownloadUrlByShareLink(String awsShareUuid) {
        Attachment attachment = attachmentRepository.findByAwsShareUuidAndValid(awsShareUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공유 링크입니다."));
        String presignedUrl = s3ServicePort.generatePresignedDownloadUrl(attachment.getS3Key());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(presignedUrlExpiration);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 첨부파일 논리 삭제
     */
    @Transactional
    public void deleteAttachment(Long boardId, Long attachmentId, Long userId) {
        // 보드 조회 및 권한 확인
        Board board = validateAttachmentAndBoardAccess(boardId, userId);

        Attachment attachment = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(attachmentId, board)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        attachment.softDelete();
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 최대 100MB를 초과할 수 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 보드 조회 및 권한 검증
     * 보드를 조회하고 사용자가 해당 보드에 접근할 권한이 있는지 확인합니다.
     * 
     * @param boardId 보드 ID
     * @param userId 사용자 ID
     * @return 조회된 보드 엔티티
     * @throws IllegalArgumentException 보드를 찾을 수 없거나 접근 권한이 없는 경우
     */
    private Board validateAttachmentAndBoardAccess(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        if (!board.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        return board;
    }

    /**
     * DTO 변환
     */
    private AttachmentResponse toResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originFilename(attachment.getOriginFilename())
                .fileSize(attachment.getFileSize())
                .contentType(attachment.getContentType())
                .uploaderId(attachment.getUploader() != null ? attachment.getUploader().getId() : null)
                .uploaderNickname(attachment.getUploader() != null ? attachment.getUploader().getNickname() : null)
                .isDeleted(attachment.isDeleted())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}

