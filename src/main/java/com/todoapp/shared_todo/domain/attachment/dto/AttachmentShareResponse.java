package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentShareResponse {

    private String shareUuid;
    private String shareUrl;
}

