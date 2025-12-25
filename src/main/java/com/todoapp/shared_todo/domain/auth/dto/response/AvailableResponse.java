package com.todoapp.shared_todo.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AvailableResponse {

    private boolean isAvailable;
    private String message;      // 프론트에서 바로 띄울 메시지 (옵션)

}
