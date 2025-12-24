package com.todoapp.shared_todo.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

//응답용, 비밀번호는 보안상 노출 금지
@Getter
@Builder
public class TokenResponse {
    //Access + Refresh Token

}
