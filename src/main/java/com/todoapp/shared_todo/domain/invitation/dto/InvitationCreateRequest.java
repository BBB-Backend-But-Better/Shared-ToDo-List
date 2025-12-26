package com.todoapp.shared_todo.domain.invitation.dto;

public record InvitationCreateRequest(
        Long boardId,
        String userCode
) {
}
