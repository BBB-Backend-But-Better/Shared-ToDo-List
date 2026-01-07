package com.todoapp.shared_todo.domain.attachment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentShareRequest {

    @NotNull(message = "TTL은 필수입니다.")
    @Min(value = 1, message = "TTL은 최소 1분 이상이어야 합니다.")
    @Max(value = 10080, message = "TTL은 최대 10080분(7일)을 초과할 수 없습니다.")
    private Integer ttlMinutes;
}

