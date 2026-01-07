package com.todoapp.shared_todo.domain.attachment.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 첨부파일 엔티티
 */
@Getter
@Entity
@Table(name = "attachment", indexes = {
        @Index(name = "idx_attachment_board_id", columnList = "board_id"),
        @Index(name = "idx_attachment_aws_share_uuid", columnList = "aws_share_uuid")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    /**
     * 원본 파일명
     * 사용자가 업로드한 원본 파일명 (UI 표시용)
     */
    @Column(name = "origin_filename", nullable = false, length = 255)
    private String originFilename;

    /**
     * 저장 파일명
     * UUID 기반으로 생성된 저장 파일명 (파일명 충돌 방지 및 보안 목적)
     */
    @Column(name = "store_filename", nullable = false, length = 255)
    private String storeFilename;

    /**
     * AWS S3 객체 키
     * S3 버킷 내부에서 파일을 식별하는 고유 경로
     * 예: "attachments/2024/01/uuid-filename.pdf"
     */
    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    /**
     * AWS S3 Public URL (선택적)
     * 공개 접근이 허용된 경우에만 사용하는 S3 Public URL
     * 현재는 사용하지 않으며, Presigned URL을 동적으로 생성하여 사용
     */
    @Column(name = "s3_url", length = 500)
    private String s3Url;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // AWS S3 공유 링크 관련 필드
    /**
     * AWS S3 파일 공유를 위한 UUID
     * 인증 없이 S3 파일에 접근할 수 있는 공개 링크의 고유 식별자
     * 예: "550e8400-e29b-41d4-a716-446655440000"
     */
    /**
     * AWS S3 공유 링크 UUID
     * 인증 없이 S3 파일에 접근할 수 있는 공개 링크의 고유 식별자
     * null이 아닌 경우 공유 링크가 활성화된 것으로 간주
     * null인 경우 공유 링크가 비활성화된 것으로 간주
     * 예: "550e8400-e29b-41d4-a716-446655440000"
     */
    @Column(name = "aws_share_uuid", unique = true, length = 36)
    private String awsShareUuid;

    /**
     * 정적 팩토리 메서드
     */
    public static Attachment create(Board board, User uploader, String originFilename,
                                     String storeFilename, String s3Key, String s3Url,
                                     Long fileSize, String contentType) {
        Attachment attachment = new Attachment();
        attachment.board = board;
        attachment.uploader = uploader;
        attachment.originFilename = originFilename;
        attachment.storeFilename = storeFilename;
        attachment.s3Key = s3Key;
        attachment.s3Url = s3Url;
        attachment.fileSize = fileSize;
        attachment.contentType = contentType;
        attachment.isDeleted = false;
        return attachment;
    }

    /**
     * 논리 삭제
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 복구
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /**
     * AWS S3 공유 링크 생성
     * S3에 저장된 파일을 인증 없이 공개적으로 접근할 수 있는 링크를 생성합니다.
     * 영구 링크로 생성되며, UUID를 통해 접근할 수 있습니다.
     */
    public void createAwsShareLink() {
        this.awsShareUuid = UUID.randomUUID().toString();
    }

    /**
     * AWS S3 공유 링크 비활성화
     * 공유 링크를 즉시 비활성화하여 더 이상 접근할 수 없도록 합니다.
     * UUID를 null로 설정하여 비활성화합니다.
     */
    public void deactivateAwsShareLink() {
        this.awsShareUuid = null;
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

