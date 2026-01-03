package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 첨부파일 정보 응답 DTO
 * Presigned URL은 별도 API로 발급받아야 함
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentResponse {

    private Long id;
    private String originFilename;
    private Long fileSize;
    private String contentType;
    private Long uploaderId;
    private String uploaderNickname;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}

