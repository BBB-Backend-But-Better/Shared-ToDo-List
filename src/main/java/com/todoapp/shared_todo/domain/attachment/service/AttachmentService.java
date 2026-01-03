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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Attachment 서비스
 * 파일 업로드/다운로드 및 공유 링크 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;
    private final S3ServicePort s3ServicePort;
    private final ShareLinkService shareLinkService;

    // 파일 크기 제한 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Presigned URL 만료 시간 (초 단위, 기본 1시간)
    private static final int PRESIGNED_URL_EXPIRATION_HOURS = 1;

    /**
     * 파일 업로드
     * @param boardId 보드 ID
     * @param userId 업로더 ID
     * @param file 업로드할 파일
     * @return AttachmentResponse
     */
    @Transactional
    public AttachmentResponse uploadFile(Long boardId, Long userId, MultipartFile file) {
        // 파일 검증
        validateFile(file);

        // 보드 및 사용자 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자만 업로드 가능
        if (!board.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드에 파일을 업로드할 권한이 없습니다.");
        }

        User uploader = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // S3 Key 및 저장 파일명 생성
        String s3Key = s3ServicePort.generateS3Key(file.getOriginalFilename());
        String storeFilename = s3ServicePort.generateStoreFilename(file.getOriginalFilename());

        try {
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
                    uploader,
                    file.getOriginalFilename(),
                    storeFilename,
                    s3Key,
                    null, // s3Url은 공개 접근 시에만 설정
                    file.getSize(),
                    file.getContentType()
            );

            Attachment savedAttachment = attachmentRepository.save(attachment);

            log.info("파일 업로드 완료: attachmentId={}, boardId={}, userId={}", savedAttachment.getId(), boardId, userId);

            return toResponse(savedAttachment);
        } catch (IOException e) {
            log.error("파일 업로드 중 IOException 발생: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 보드별 첨부파일 목록 조회
     * @param boardId 보드 ID
     * @param userId 사용자 ID
     * @return AttachmentListResponse
     */
    public AttachmentListResponse getAttachments(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자만 조회 가능
        if (!board.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드의 첨부파일을 조회할 권한이 없습니다.");
        }

        List<Attachment> attachments = attachmentRepository.findByBoardIdAndIsDeletedFalse(boardId);

        List<AttachmentResponse> attachmentResponses = attachments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return AttachmentListResponse.builder()
                .attachments(attachmentResponses)
                .totalCount(attachmentResponses.size())
                .build();
    }

    /**
     * 첨부파일 단건 조회
     * @param boardId 보드 ID
     * @param attachmentId 첨부파일 ID
     * @param userId 사용자 ID
     * @return AttachmentResponse
     */
    public AttachmentResponse getAttachment(Long boardId, Long attachmentId, Long userId) {
        Attachment attachment = validateAttachmentAndBoardAccess(boardId, attachmentId, userId);
        return toResponse(attachment);
    }

    /**
     * Presigned URL 생성 (다운로드용)
     * @param boardId 보드 ID
     * @param attachmentId 첨부파일 ID
     * @param userId 사용자 ID
     * @return PresignedUrlResponse
     */
    public PresignedUrlResponse generatePresignedDownloadUrl(Long boardId, Long attachmentId, Long userId) {
        Attachment attachment = validateAttachmentAndBoardAccess(boardId, attachmentId, userId);

        String presignedUrl = s3ServicePort.generatePresignedDownloadUrl(attachment.getS3Key());
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(PRESIGNED_URL_EXPIRATION_HOURS);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 공유 링크 생성
     * @param boardId 보드 ID
     * @param attachmentId 첨부파일 ID
     * @param userId 사용자 ID
     * @param request 공유 링크 생성 요청 (TTL 포함)
     * @return AttachmentShareResponse
     */
    @Transactional
    public AttachmentShareResponse createShareLink(Long boardId, Long attachmentId, Long userId, AttachmentShareRequest request) {
        // 권한 검증 및 첨부파일 조회
        Attachment attachment = validateAttachmentAndBoardAccess(boardId, attachmentId, userId);

        // 공유 링크 생성 (DB에 저장)
        String shareUuid = shareLinkService.createShareLink(attachment, request.getTtlMinutes());
        String shareUrl = shareLinkService.generateShareUrl(shareUuid);
        LocalDateTime expiresAt = shareLinkService.getExpiresAt(shareUuid);

        log.info("공유 링크 생성 완료: attachmentId={}, shareUuid={}", attachmentId, shareUuid);

        return AttachmentShareResponse.builder()
                .shareUuid(shareUuid)
                .shareUrl(shareUrl)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 공개 링크로 첨부파일 조회 (인증 불필요)
     * @param shareUuid 공유 링크 UUID
     * @return AttachmentResponse
     */
    public AttachmentResponse getAttachmentByShareLink(String shareUuid) {
        // DB에서 첨부파일 조회
        Attachment attachment = shareLinkService.getAttachmentByShareUuid(shareUuid);
        if (attachment == null) {
            throw new IllegalArgumentException("공유 링크가 만료되었거나 존재하지 않습니다.");
        }

        // 논리 삭제 확인
        if (attachment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 첨부파일입니다.");
        }

        return toResponse(attachment);
    }

    /**
     * 공개 링크로 Presigned URL 생성 (인증 불필요)
     * @param shareUuid 공유 링크 UUID
     * @return PresignedUrlResponse
     */
    public PresignedUrlResponse generatePresignedDownloadUrlByShareLink(String shareUuid) {
        // DB에서 첨부파일 조회
        Attachment attachment = shareLinkService.getAttachmentByShareUuid(shareUuid);
        if (attachment == null) {
            throw new IllegalArgumentException("공유 링크가 만료되었거나 존재하지 않습니다.");
        }

        // 논리 삭제 확인
        if (attachment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 첨부파일입니다.");
        }

        String presignedUrl = s3ServicePort.generatePresignedDownloadUrl(attachment.getS3Key());
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(PRESIGNED_URL_EXPIRATION_HOURS);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 첨부파일 삭제 (논리 삭제)
     * @param boardId 보드 ID
     * @param attachmentId 첨부파일 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAttachment(Long boardId, Long attachmentId, Long userId) {
        Attachment attachment = validateAttachmentAndBoardAccess(boardId, attachmentId, userId);

        // 논리 삭제 처리
        attachment.softDelete();
        attachmentRepository.save(attachment);

        log.info("첨부파일 논리 삭제 완료: attachmentId={}, boardId={}", attachmentId, boardId);
    }

    /**
     * 첨부파일 및 보드 접근 권한 검증
     */
    private Attachment validateAttachmentAndBoardAccess(Long boardId, Long attachmentId, Long userId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        // 논리 삭제 확인
        if (attachment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 첨부파일입니다.");
        }

        // boardId 검증
        if (!attachment.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("첨부파일이 해당 보드에 속하지 않습니다.");
        }

        // 권한 확인: 소유자만 접근 가능
        if (!attachment.getBoard().getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        return attachment;
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 파일 확장자 검증 (선택적)
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }
    }

    /**
     * Attachment 엔티티를 Response DTO로 변환
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

