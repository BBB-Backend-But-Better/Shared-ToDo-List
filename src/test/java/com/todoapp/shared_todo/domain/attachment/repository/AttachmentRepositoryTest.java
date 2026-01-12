package com.todoapp.shared_todo.domain.attachment.repository;

import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Attachment Repository 통합 테스트
 * JPA 쿼리 및 DB 연동 검증
 */
@DataJpaTest
@DisplayName("Attachment Repository 테스트")
@SuppressWarnings("unused")
class AttachmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private Board board;
    private Attachment attachment1;
    private Attachment attachment2;
    private Attachment deletedAttachment;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .loginId("testuser")
                .password("password")
                .nickname("Test User")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        user = entityManager.persistAndFlush(user);

        board = Board.create("Test Board", user);
        board = entityManager.persistAndFlush(board);

        attachment1 = Attachment.create(
                board, user, "file1.pdf", "uuid-file1.pdf",
                "attachments/2024/01/uuid-file1.pdf", null, 1024L, "application/pdf"
        );
        attachment1 = entityManager.persistAndFlush(attachment1);

        attachment2 = Attachment.create(
                board, user, "file2.jpg", "uuid-file2.jpg",
                "attachments/2024/01/uuid-file2.jpg", null, 2048L, "image/jpeg"
        );
        attachment2 = entityManager.persistAndFlush(attachment2);

        deletedAttachment = Attachment.create(
                board, user, "deleted.pdf", "uuid-deleted.pdf",
                "attachments/2024/01/uuid-deleted.pdf", null, 512L, "application/pdf"
        );
        deletedAttachment.softDelete();
        deletedAttachment = entityManager.persistAndFlush(deletedAttachment);
    }

    @Test
    @DisplayName("보드별 첨부파일 목록 조회 (논리 삭제 제외)")
    void findByBoardAndIsDeletedFalse_success() {
        List<Attachment> attachments = attachmentRepository.findByBoardAndIsDeletedFalse(board);

        assertThat(attachments).hasSize(2);
        assertThat(attachments).containsExactlyInAnyOrder(attachment1, attachment2);
        assertThat(attachments).noneMatch(Attachment::isDeleted);
    }

    @Test
    @DisplayName("보드 ID로 첨부파일 목록 조회 (논리 삭제 제외)")
    void findByBoardIdAndIsDeletedFalse_success() {
        List<Attachment> attachments = attachmentRepository.findByBoardIdAndIsDeletedFalse(board.getId());

        assertThat(attachments).hasSize(2);
        assertThat(attachments).containsExactlyInAnyOrder(attachment1, attachment2);
    }

    @Test
    @DisplayName("첨부파일 ID와 보드로 조회 (논리 삭제 제외)")
    void findByIdAndBoardAndIsDeletedFalse_success() {
        Optional<Attachment> found = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(
                attachment1.getId(), board
        );

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(attachment1);
        assertThat(found.get().isDeleted()).isFalse();
    }

    @Test
    @DisplayName("논리 삭제된 첨부파일은 조회되지 않음")
    void findByIdAndBoardAndIsDeletedFalse_logicallyDeletedFile_notFound() {
        Optional<Attachment> found = attachmentRepository.findByIdAndBoardAndIsDeletedFalse(
                deletedAttachment.getId(), board
        );

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("논리 삭제된 첨부파일 목록 조회")
    void findAllByIsDeletedTrue_success() {
        List<Attachment> deletedAttachments = attachmentRepository.findAllByIsDeletedTrue();

        assertThat(deletedAttachments).hasSize(1);
        assertThat(deletedAttachments).contains(deletedAttachment);
    }

    @Test
    @DisplayName("AWS S3 공유 UUID로 유효한 첨부파일 조회")
    void findByAwsShareUuidAndValid_success() {
        attachment1.createAwsShareLink();
        entityManager.persistAndFlush(attachment1);

        Optional<Attachment> found = attachmentRepository.findByAwsShareUuidAndValid(
                attachment1.getAwsShareUuid()
        );

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(attachment1);
        assertThat(found.get().getAwsShareUuid()).isNotNull();
    }

    @Test
    @DisplayName("비활성화된 AWS S3 공유 링크는 조회되지 않음")
    void findByAwsShareUuidAndValid_deactivatedLink_notFound() {
        attachment1.createAwsShareLink();
        String shareUuid = attachment1.getAwsShareUuid();
        attachment1.deactivateAwsShareLink(); // UUID를 null로 설정
        entityManager.persistAndFlush(attachment1);

        Optional<Attachment> found = attachmentRepository.findByAwsShareUuidAndValid(shareUuid);

        assertThat(found).isEmpty();
    }
}

