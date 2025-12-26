package com.todoapp.shared_todo.domain.invitation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCreateRequest {

    private Long boardId;
    private String userCode;
}
