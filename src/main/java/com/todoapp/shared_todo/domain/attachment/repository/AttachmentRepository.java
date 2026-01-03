package com.todoapp.shared_todo.domain.attachment.repository;

import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused") // Spring Data JPA가 런타임에 자동으로 구현하여 사용
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * 보드별 첨부파일 목록 조회 (논리 삭제 제외)
     * @deprecated 현재는 findByBoardIdAndIsDeletedFalse 사용
     */
    @Deprecated
    List<Attachment> findByBoardAndIsDeletedFalse(Board board);

    /**
     * 보드 ID로 첨부파일 목록 조회 (논리 삭제 제외)
     */
    List<Attachment> findByBoardIdAndIsDeletedFalse(Long boardId);

    /**
     * 첨부파일 ID와 보드로 조회 (권한 확인용, 논리 삭제 제외)
     * 향후 권한 검증 로직 개선 시 사용 예정
     */
    Optional<Attachment> findByIdAndBoardAndIsDeletedFalse(Long id, Board board);

    /**
     * 업로더별 첨부파일 목록 조회 (논리 삭제 제외)
     * 향후 사용자별 파일 관리 기능 추가 시 사용 예정
     */
    List<Attachment> findByUploaderAndIsDeletedFalse(User uploader);

    /**
     * S3 Key로 첨부파일 조회 (Presigned URL 재발급용)
     * 향후 S3 Key 기반 조회 기능 추가 시 사용 예정
     */
    Optional<Attachment> findByS3KeyAndIsDeletedFalse(String s3Key);

    /**
     * 논리 삭제된 첨부파일 목록 조회 (Batch 작업용)
     * Spring Batch 작업에서 사용 예정
     */
    List<Attachment> findAllByIsDeletedTrue();

    /**
     * 특정 시각 이전에 논리 삭제된 첨부파일 목록 조회 (Batch 작업용)
     * 실제 S3 객체 삭제 대상 선정에 사용
     * Spring Batch 작업에서 사용 예정
     */
    List<Attachment> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime before);

    /**
     * 보드별 논리 삭제된 첨부파일 목록 조회 (보드 삭제 시 정리용)
     * 보드 삭제 시 연관 첨부파일 정리 기능 추가 시 사용 예정
     */
    List<Attachment> findByBoardAndIsDeletedTrue(Board board);
}

