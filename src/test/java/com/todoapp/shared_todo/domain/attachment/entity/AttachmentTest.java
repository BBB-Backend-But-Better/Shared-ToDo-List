package com.todoapp.shared_todo.domain.attachment.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Attachment Entity 단위 테스트
 * 비즈니스 로직 검증 (create, softDelete, restore, createAwsShareLink 등)
 */
@DisplayName("Attachment Entity 테스트")
class AttachmentTest {

    private Board board;
    private User uploader;
    private String originFilename;
    private String storeFilename;
    private String s3Key;
    private Long fileSize;
    private String contentType;

    @BeforeEach
    void setUp() {
        uploader = User.builder()
                .loginId("testuser")
                .password("password")
                .nickname("Test User")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(uploader, "id", 1L);

        board = Board.create("Test Board", uploader);
        board.setId(1L);

        originFilename = "test-file.pdf";
        storeFilename = "uuid-test-file.pdf";
        s3Key = "attachments/2024/01/uuid-test-file.pdf";
        fileSize = 1024L;
        contentType = "application/pdf";
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 Attachment 생성 성공")
    void create_success() {
        Attachment attachment = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );

        assertThat(attachment.getBoard()).isEqualTo(board);
        assertThat(attachment.getUploader()).isEqualTo(uploader);
        assertThat(attachment.getOriginFilename()).isEqualTo(originFilename);
        assertThat(attachment.getStoreFilename()).isEqualTo(storeFilename);
        assertThat(attachment.getS3Key()).isEqualTo(s3Key);
        assertThat(attachment.getFileSize()).isEqualTo(fileSize);
        assertThat(attachment.getContentType()).isEqualTo(contentType);
        assertThat(attachment.isDeleted()).isFalse();
        assertThat(attachment.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("논리 삭제 성공")
    void softDelete_success() {
        Attachment attachment = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );

        attachment.softDelete();

        assertThat(attachment.isDeleted()).isTrue();
        assertThat(attachment.getDeletedAt()).isNotNull();
        assertThat(attachment.getDeletedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("복구 성공")
    void restore_success() {
        Attachment attachment = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );
        attachment.softDelete();

        attachment.restore();

        assertThat(attachment.isDeleted()).isFalse();
        assertThat(attachment.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("AWS S3 공유 링크 생성 성공")
    void createAwsShareLink_success() {
        Attachment attachment = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );

        attachment.createAwsShareLink();

        assertThat(attachment.getAwsShareUuid()).isNotNull();
        assertThat(attachment.getAwsShareUuid()).hasSize(36); // UUID 형식
    }

    @Test
    @DisplayName("AWS S3 공유 링크 비활성화 성공")
    void deactivateAwsShareLink_success() {
        Attachment attachment = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );
        attachment.createAwsShareLink();

        attachment.deactivateAwsShareLink();

        assertThat(attachment.getAwsShareUuid()).isNull(); // UUID가 null로 설정됨
    }

    @Test
    @DisplayName("equals와 hashCode 테스트")
    void equalsAndHashCode_test() {
        Attachment attachment1 = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );
        ReflectionTestUtils.setField(attachment1, "id", 1L);

        Attachment attachment2 = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );
        ReflectionTestUtils.setField(attachment2, "id", 1L);

        Attachment attachment3 = Attachment.create(
                board, uploader, originFilename, storeFilename, s3Key, null, fileSize, contentType
        );
        ReflectionTestUtils.setField(attachment3, "id", 2L);

        assertThat(attachment1).isEqualTo(attachment2);
        assertThat(attachment1).isNotEqualTo(attachment3);
        assertThat(attachment1.hashCode()).isEqualTo(attachment2.hashCode());
    }
}

