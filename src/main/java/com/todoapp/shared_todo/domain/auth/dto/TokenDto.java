package com.todoapp.shared_todo.domain.auth.dto;


public record TokenDto(
        String accessToken,
        String refreshToken // RTR 적용 시 필수!
) {
}
