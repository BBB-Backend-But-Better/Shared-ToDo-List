package com.todoapp.shared_todo.domain.attachment.entity;

import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 공유 링크 엔티티
 * UUID 기반 공개 링크 관리
 */
@Getter
@Setter
@Entity
@Table(name = "share_link", indexes = {
        @Index(name = "idx_share_link_uuid", columnList = "share_uuid", unique = true),
        @Index(name = "idx_share_link_attachment", columnList = "attachment_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareLink extends BaseTimeEntity {

    /**
     * 정적 팩토리 메서드
     * 공유 링크 생성 시 사용
     */
    public static ShareLink create(Attachment attachment, Integer ttlMinutes) {
        ShareLink shareLink = new ShareLink();
        shareLink.setAttachment(attachment);
        shareLink.setShareUuid(UUID.randomUUID().toString());
        shareLink.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        shareLink.isActive = true;
        return shareLink;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공유 링크 UUID
     * 공개 접근을 위한 고유 식별자
     */
    @Column(name = "share_uuid", length = 36, nullable = false, unique = true)
    private String shareUuid;

    /**
     * 첨부파일
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;

    /**
     * 만료 시각
     * 이 시각 이후에는 링크가 유효하지 않음
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 활성화 여부
     * 수동으로 비활성화할 수 있음
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * 공유 링크 유효성 검증
     * @return 유효 여부
     */
    public boolean isValid() {
        return isActive && expiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * 공유 링크 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 활성화 여부 조회
     */
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareLink shareLink = (ShareLink) o;
        return Objects.equals(id, shareLink.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

