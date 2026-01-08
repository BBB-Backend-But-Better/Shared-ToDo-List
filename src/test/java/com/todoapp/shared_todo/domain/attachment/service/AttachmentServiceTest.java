package com.todoapp.shared_todo.domain.attachment.service;

import com.todoapp.shared_todo.domain.attachment.dto.*;
import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.attachment.port.S3ServicePort;
import com.todoapp.shared_todo.domain.attachment.repository.AttachmentRepository;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

/**
 * Attachment Service 단위 테스트
 * 외부 의존성(S3ServicePort, Repository)을 Mock으로 처리
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Attachment Service 테스트")
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private S3ServicePort s3ServicePort;

    @InjectMocks
    private AttachmentService attachmentService;

    private User user;
    private Board board;
    private Attachment attachment;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .loginId("testuser")
                .password("password")
                .nickname("Test User")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        board = Board.create("Test Board", user);
        board.setId(1L);

        attachment = Attachment.create(
                board, user, "test.pdf", "uuid-test.pdf",
                "attachments/2024/01/uuid-test.pdf", null, 1024L, "application/pdf"
        );
        ReflectionTestUtils.setField(attachment, "id", 1L);

        multipartFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test content".getBytes()
        );

        ReflectionTestUtils.setField(attachmentService, "presignedUrlExpiration", 3600);
        ReflectionTestUtils.setField(attachmentService, "shareLinkBaseUrl", "http://localhost:8080/api/public/attachments");
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFile_success() throws IOException {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(usersRepository.findById(1L)).willReturn(Optional.of(user));
        given(s3ServicePort.generateS3Key(anyString())).willReturn("attachments/2024/01/uuid-test.pdf");
        given(s3ServicePort.generateStoreFilename(anyString())).willReturn("uuid-test.pdf");
        willDoNothing().given(s3ServicePort).uploadFile(any(), anyString(), anyString(), any(Long.class));
        given(attachmentRepository.save(any(Attachment.class))).willReturn(attachment);

        AttachmentResponse response = attachmentService.uploadFile(1L, 1L, multipartFile);

        assertThat(response).isNotNull();
        assertThat(response.getOriginFilename()).isEqualTo("test.pdf");
        verify(s3ServicePort).uploadFile(any(), anyString(), anyString(), any(Long.class));
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 크기 초과")
    void uploadFile_failure_fileSizeExceeded() {
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "large.pdf", "application/pdf", new byte[101 * 1024 * 1024] // 101MB
        );


        assertThatThrownBy(() -> attachmentService.uploadFile(1L, 1L, largeFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일 크기는 최대 100MB");
    }

    @Test
    @DisplayName("파일 업로드 실패 - 허용되지 않은 확장자")
    void uploadFile_failure_invalidExtension() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.exe", "application/x-msdownload", "test content".getBytes()
        );

        assertThatThrownBy(() -> attachmentService.uploadFile(1L, 1L, invalidFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않은 파일 형식");
    }

    @Test
    @DisplayName("보드별 첨부파일 목록 조회 성공")
    void getAttachments_success() {
        Attachment attachment2 = Attachment.create(
                board, user, "test2.jpg", "uuid-test2.jpg",
                "attachments/2024/01/uuid-test2.jpg", null, 2048L, "image/jpeg"
        );
        ReflectionTestUtils.setField(attachment2, "id", 2L);

        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByBoardAndIsDeletedFalse(board))
                .willReturn(List.of(attachment, attachment2));

        AttachmentListResponse response = attachmentService.getAttachments(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getAttachments()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("첨부파일 단건 조회 성공")
    void getAttachment_success() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.of(attachment));

        AttachmentResponse response = attachmentService.getAttachment(1L, 1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOriginFilename()).isEqualTo("test.pdf");
    }

    @Test
    @DisplayName("첨부파일 단건 조회 실패 - 첨부파일 없음")
    void getAttachment_failure_attachmentNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getAttachment(1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("첨부파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("Presigned URL 생성 성공")
    void generatePresignedDownloadUrl_success() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.of(attachment));
        given(s3ServicePort.generatePresignedDownloadUrl(anyString()))
                .willReturn("https://s3.amazonaws.com/presigned-url");

        PresignedUrlResponse response = attachmentService.generatePresignedDownloadUrl(1L, 1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getPresignedUrl()).isNotNull();
        assertThat(response.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Presigned URL 생성 실패 - 첨부파일 없음")
    void generatePresignedDownloadUrl_failure_attachmentNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.generatePresignedDownloadUrl(1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("첨부파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("AWS S3 공유 링크 생성 성공")
    void createAwsShareLink_success() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.of(attachment));
        given(attachmentRepository.save(any(Attachment.class))).willAnswer(invocation -> {
            Attachment saved = invocation.getArgument(0);
            saved.createAwsShareLink();
            return saved;
        });

        AttachmentShareResponse response = attachmentService.createAwsShareLink(1L, 1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getShareUuid()).isNotNull();
        assertThat(response.getShareUrl()).contains(response.getShareUuid());
    }

    @Test
    @DisplayName("AWS S3 공유 링크 생성 실패 - 첨부파일 없음")
    void createAwsShareLink_failure_attachmentNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.createAwsShareLink(1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("첨부파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("AWS S3 공유 링크로 첨부파일 조회 성공")
    void getAttachmentByAwsShareLink_success() {
        attachment.createAwsShareLink();
        given(attachmentRepository.findByAwsShareUuidAndValid(anyString()))
                .willReturn(Optional.of(attachment));

        AttachmentResponse response = attachmentService.getAttachmentByAwsShareLink(attachment.getAwsShareUuid());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("AWS S3 공유 링크로 첨부파일 조회 실패 - 유효하지 않은 링크")
    void getAttachmentByAwsShareLink_failure_invalidLink() {
        given(attachmentRepository.findByAwsShareUuidAndValid(anyString()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getAttachmentByAwsShareLink("invalid-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 공유 링크");
    }

    @Test
    @DisplayName("첨부파일 논리 삭제 성공")
    void deleteAttachment_success() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.of(attachment));

        attachmentService.deleteAttachment(1L, 1L, 1L);

        assertThat(attachment.isDeleted()).isTrue();
        verify(attachmentRepository).findByIdAndBoardAndIsDeletedFalse(1L, board);
    }

    @Test
    @DisplayName("첨부파일 논리 삭제 실패 - 첨부파일 없음")
    void deleteAttachment_failure_attachmentNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(attachmentRepository.findByIdAndBoardAndIsDeletedFalse(1L, board))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.deleteAttachment(1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("첨부파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("권한 검증 실패 - 보드 소유자가 아님")
    void validateAttachmentAndBoardAccess_failure_noPermission() {
        User otherUser = User.builder()
                .loginId("otheruser")
                .password("password")
                .nickname("Other User")
                .userCode("USER002")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        Board otherBoard = Board.create("Other Board", otherUser);
        otherBoard.setId(2L);

        given(boardRepository.findById(2L)).willReturn(Optional.of(otherBoard));

        assertThatThrownBy(() -> attachmentService.getAttachments(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드에 접근할 권한이 없습니다");
    }
}

