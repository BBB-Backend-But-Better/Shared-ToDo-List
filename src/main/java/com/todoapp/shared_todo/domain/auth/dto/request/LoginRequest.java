package com.todoapp.shared_todo.domain.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest(
        @NotBlank(message = "아이디를 입력해 주세요.")
        @Schema(description = "사용자 로그인 ID", example = "myuser1234")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Schema(description = "비밀번호", example = "password123!")
        String password) {
}