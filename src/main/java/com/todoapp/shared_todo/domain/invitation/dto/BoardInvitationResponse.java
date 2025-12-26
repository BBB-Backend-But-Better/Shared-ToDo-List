package com.todoapp.shared_todo.domain.invitation.dto;

import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;

import java.time.LocalDateTime;

public record BoardInvitationResponse(
        Long invitationId,
        String inviteeName,
        String inviteeUserCode,
        InvitationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}