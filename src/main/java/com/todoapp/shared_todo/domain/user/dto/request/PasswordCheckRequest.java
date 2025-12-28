package com.todoapp.shared_todo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record PasswordCheckRequest(
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Size(message = "비밀번호를 확인해주세요")
        String password
) {
}
