package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentListResponse {

    private List<AttachmentResponse> attachments;
    private Long totalCount;
}

