package com.todoapp.shared_todo.domain.attachment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공유 링크 생성 요청 DTO
 * UUID 기반 공개 링크 생성 시 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentShareRequest {

    /**
     * 공유 링크 유효 기간 (분 단위)
     * Redis TTL 설정에 사용
     * 최소 1분, 최대 10080분(7일) 권장
     */
    @NotNull(message = "유효 기간은 필수입니다.")
    @Min(value = 1, message = "유효 기간은 최소 1분 이상이어야 합니다.")
    @Max(value = 10080, message = "유효 기간은 최대 7일입니다.")
    private Integer ttlMinutes;
}

