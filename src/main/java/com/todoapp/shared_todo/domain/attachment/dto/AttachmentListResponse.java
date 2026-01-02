package com.todoapp.shared_todo.domain.attachment.dto;

import lombok.*;

import java.util.List;

/**
 * 첨부파일 목록 응답 DTO
 * 보드별 첨부파일 조회 시 사용
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentListResponse {

    private List<AttachmentResponse> attachments;
    private Integer totalCount;
}

