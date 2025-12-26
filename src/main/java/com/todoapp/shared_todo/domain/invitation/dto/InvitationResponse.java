package com.todoapp.shared_todo.domain.invitation.dto;

import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;

import java.time.LocalDateTime;

public record InvitationResponse(
        Long invitationId,
        Long boardId,
        String boardTitle,
        String inviterName,
        InvitationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}