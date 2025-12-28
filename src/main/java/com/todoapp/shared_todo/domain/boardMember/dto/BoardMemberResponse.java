package com.todoapp.shared_todo.domain.boardMember.dto;

import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;

public record BoardMemberResponse(
        Long userId,
        String nickname,
        BoardMemberRole role
) {
}