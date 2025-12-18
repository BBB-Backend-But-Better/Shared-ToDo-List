package com.todoapp.shared_todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {

    //회원 가입 요청
    public static class SignupRequestDto{
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 15, message = "아이디는 4 ~ 15자 사이여야 합니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 20, message = "비밀번호는 4 ~ 20자 사이여야 합니다.")
        private String password;
    }

    //로그인 요청
    public static class LoginRequestDto{
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    //응답용, 비밀번호는 보안상 노출 금지
    public static class UserRequestDto{
        private final  String loginId;
        private final String password;

        public UserRequestDto(String loginId, String password) {
            this.loginId = loginId;
            this.password = password;
        }
    }

}
