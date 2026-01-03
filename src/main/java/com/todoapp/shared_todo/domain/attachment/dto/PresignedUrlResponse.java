package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Presigned URL 응답 DTO
 * S3 직접 다운로드를 위한 임시 접근 URL
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PresignedUrlResponse {

    /**
     * Presigned URL
     * 제한된 시간 동안만 유효한 S3 접근 URL
     */
    private String presignedUrl;

    /**
     * URL 만료 시각
     * 클라이언트에서 캐싱 전략 수립에 활용
     */
    private LocalDateTime expiresAt;
}

