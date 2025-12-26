package com.todoapp.shared_todo.domain.invitation.dto;

import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardInvitationResponse {

    private Long invitationId;
    // 초대 받은 사용자 정보
    private String receiverName;
    private String receiverUserCode;
    private InvitationStatus status;
    private LocalDateTime createdAt;
}
