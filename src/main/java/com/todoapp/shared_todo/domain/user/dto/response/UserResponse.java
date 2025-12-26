package com.todoapp.shared_todo.domain.user.dto.response;

import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;

public record UserResponse(
        Long id,
        String loginId,
        String nickname,
        String userCode,
        ProviderType provider

) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getUserCode(),
                user.getProvider());
    }
}
