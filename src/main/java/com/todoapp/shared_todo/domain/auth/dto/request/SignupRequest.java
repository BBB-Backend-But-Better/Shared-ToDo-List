package com.todoapp.shared_todo.domain.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record SignupRequest(
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Size(min = 4, max = 15, message = "아이디는 4~15자 사이여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "아이디는 영문, 숫자만 사용 가능합니다."
        )
        String loginId,

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Size(min = 2, max = 15, message = "닉네임은 2~15자 사이여야 합니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@!*])[A-Za-z\\d@!*]{8,20}$",
                message = "비밀번호는 8~20자의 영문, 숫자, 특수문자(@!*)를 포함해야 합니다."
        )
        String password) {}