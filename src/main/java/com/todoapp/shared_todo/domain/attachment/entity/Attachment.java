package com.todoapp.shared_todo.domain.attachment.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "attachment", indexes = {
        @Index(name = "idx_attachment_board_id", columnList = "board_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseTimeEntity {

    /**
     * 정적 팩토리 메서드
     * 첨부파일 생성 시 사용
     */
    public static Attachment create(
            Board board,
            User uploader,
            String originFilename,
            String storeFilename,
            String s3Key,
            String s3Url,
            Long fileSize,
            String contentType) {
        Attachment attachment = new Attachment();
        attachment.setBoard(board);
        attachment.setUploader(uploader);
        attachment.setOriginFilename(originFilename);
        attachment.setStoreFilename(storeFilename);
        attachment.setS3Key(s3Key);
        attachment.setS3Url(s3Url);
        attachment.setFileSize(fileSize);
        attachment.setContentType(contentType);
        attachment.setIsDeleted(false);
        return attachment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 첨부파일이 속한 보드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /**
     * 파일을 업로드한 사용자
     * 추후 권한 검증, 감사(Audit), 책임 추적을 위해 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    /**
     * 사용자가 업로드한 원본 파일명
     * UI 표시 및 사용자 인지용이며 실제 저장에는 사용하지 않음
     */
    @Column(name = "origin_filename", length = 255, nullable = false)
    private String originFilename;

    /**
     * UUID 기반으로 생성된 저장 파일명
     * 파일명 충돌 방지 및 경로 추측 공격 방지를 위한 보안 목적
     */
    @Column(name = "store_filename", length = 255, nullable = false)
    private String storeFilename;

    /**
     * S3 버킷 내부 객체 식별자(Key)
     * URL 변경과 무관하게 객체를 유일하게 식별하며 Presigned URL 재발급의 기준
     */
    @Column(name = "s3_key", length = 500, nullable = false)
    private String s3Key;

    /**
     * 공개 접근이 허용된 경우에만 사용하는 S3 Public URL
     * Presigned URL은 동적 생성되므로 DB에 저장하지 않음
     */
    @Column(name = "s3_url", length = 500)
    private String s3Url;

    /**
     * 파일 크기(Byte)
     * 업로드 제한 검증 및 스토리지 비용 산정에 활용
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * 파일 MIME 타입
     * 다운로드 시 Content-Type 지정 및 허용 파일 검증에 사용
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 논리 삭제 여부
     * 즉시 S3 삭제 대신 유예 기간을 두어 실수 방지 및 비용 관리에 활용
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * 논리 삭제 시각
     * Batch 작업을 통해 실제 S3 객체 삭제 시점 판단 기준
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 논리 삭제 처리
     * 실제 S3 삭제는 배치 작업에서 처리
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 논리 삭제 취소 (복구)
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
