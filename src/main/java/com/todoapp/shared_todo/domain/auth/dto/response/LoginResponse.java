package com.todoapp.shared_todo.domain.auth.dto.response;


public record LoginResponse(

        String accessToken,
        String refreshToken,
        String nickname,
        String usercode
) {
}
