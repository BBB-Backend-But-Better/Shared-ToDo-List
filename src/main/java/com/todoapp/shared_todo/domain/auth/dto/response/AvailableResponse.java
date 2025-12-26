package com.todoapp.shared_todo.domain.auth.dto.response;


public record AvailableResponse(

        boolean isAvailable,
        String message   // 프론트에서 바로 띄울 메시지 (옵션)
) {
}
