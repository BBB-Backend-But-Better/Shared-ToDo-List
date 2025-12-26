package com.todoapp.shared_todo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record ChangePasswordRequest(
        @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
        @Size(max = 20, message = "비밀번호를 확인해 주세요.")
        String currentPassword,


        @NotBlank(message = "변경할 비밀번호를 입력해 주세요.")
        @Size(min = 8, max = 20, message = "아이디는 8~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@!*])[A-Za-z\\d@!*]{8,20}$",
                message = "비밀번호는 8~20자의 영문, 숫자, 특수문자(@!*)를 포함해야 합니다."
        )
        String newPassword
) {
}
