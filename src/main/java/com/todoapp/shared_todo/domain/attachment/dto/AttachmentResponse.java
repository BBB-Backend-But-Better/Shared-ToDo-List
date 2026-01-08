package com.todoapp.shared_todo.domain.attachment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class AttachmentResponse {

    private Long id;
    private String originFilename;
    private Long fileSize;
    private String contentType;
    private Long uploaderId;
    private String uploaderNickname;
    private boolean isDeleted;
    private LocalDateTime createdAt;

    public AttachmentResponse(Long id, String originFilename, Long fileSize, String contentType,
                              Long uploaderId, String uploaderNickname, boolean isDeleted, LocalDateTime createdAt) {
        this.id = id;
        this.originFilename = originFilename;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploaderId = uploaderId;
        this.uploaderNickname = uploaderNickname;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
