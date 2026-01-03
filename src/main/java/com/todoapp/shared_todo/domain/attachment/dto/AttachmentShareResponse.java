package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 공유 링크 응답 DTO
 * UUID 기반 공개 접근 링크 정보
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentShareResponse {

    /**
     * 공유 링크 UUID
     * 공개 접근을 위한 고유 식별자
     */
    private String shareUuid;

    /**
     * 공유 링크 전체 URL
     * 클라이언트에서 바로 사용 가능한 완전한 URL
     */
    private String shareUrl;

    /**
     * 링크 만료 시각
     * DB의 expires_at 필드와 동기화
     */
    private LocalDateTime expiresAt;
}

