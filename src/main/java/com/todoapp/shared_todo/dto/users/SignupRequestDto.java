package com.todoapp.shared_todo.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//회원 가입 요청
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto{
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Size(min = 4, max = 15, message = "아이디는 4 ~ 15자 사이여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 4 ~ 20자 사이여야 합니다.")
    private String password;
}