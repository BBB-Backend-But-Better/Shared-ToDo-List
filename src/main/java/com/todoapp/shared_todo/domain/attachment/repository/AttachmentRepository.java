package com.todoapp.shared_todo.domain.attachment.repository;

import com.todoapp.shared_todo.domain.attachment.entity.Attachment;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * 보드별 첨부파일 목록 조회 (논리 삭제 제외)
     */
    List<Attachment> findByBoardAndIsDeletedFalse(Board board);

    /**
     * 보드 ID로 첨부파일 목록 조회 (논리 삭제 제외)
     */
    List<Attachment> findByBoardIdAndIsDeletedFalse(Long boardId);

    /**
     * 첨부파일 ID와 보드로 조회 (권한 확인용, 논리 삭제 제외)
     */
    Optional<Attachment> findByIdAndBoardAndIsDeletedFalse(Long id, Board board);

    /**
     * 업로더별 첨부파일 목록 조회 (논리 삭제 제외)
     */
    List<Attachment> findByUploaderAndIsDeletedFalse(User uploader);

    /**
     * S3 Key로 첨부파일 조회 (논리 삭제 제외)
     */
    Optional<Attachment> findByS3KeyAndIsDeletedFalse(String s3Key);

    /**
     * 논리 삭제된 첨부파일 목록 조회 (Batch 작업용)
     */
    List<Attachment> findAllByIsDeletedTrue();

    /**
     * 특정 시각 이전 논리 삭제된 첨부파일 조회 (Batch 작업용)
     */
    @Query("SELECT a FROM Attachment a WHERE a.isDeleted = true AND a.deletedAt < :before")
    List<Attachment> findByIsDeletedTrueAndDeletedAtBefore(@Param("before") LocalDateTime before);

    /**
     * 보드별 논리 삭제된 첨부파일 조회 (보드 삭제 시 정리용)
     */
    List<Attachment> findByBoardAndIsDeletedTrue(Board board);

    /**
     * AWS S3 공유 UUID로 유효한 첨부파일 조회
     * 공유 링크를 통해 인증 없이 S3 파일에 접근할 수 있는 첨부파일을 조회합니다.
     * 
     * @param awsShareUuid AWS S3 공유 링크 UUID
     * @return 유효한 첨부파일 (논리 삭제되지 않음, 공유 링크 활성)
     */
    @Query("SELECT a FROM Attachment a WHERE a.awsShareUuid = :awsShareUuid AND a.isDeleted = false")
    Optional<Attachment> findByAwsShareUuidAndValid(@Param("awsShareUuid") String awsShareUuid);
}
