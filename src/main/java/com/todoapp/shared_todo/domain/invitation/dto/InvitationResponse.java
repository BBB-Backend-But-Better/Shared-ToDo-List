package com.todoapp.shared_todo.domain.invitation.dto;

import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class InvitationResponse {

    private Long invitationId;
    private Long boardId;
    private String boardTitle;
    // 초대한 사용자 이름
    private String senderName;
    private InvitationStatus status;
    private LocalDateTime createdAt;
}
