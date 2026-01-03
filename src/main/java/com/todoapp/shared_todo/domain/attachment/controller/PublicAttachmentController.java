package com.todoapp.shared_todo.domain.attachment.controller;

import com.todoapp.shared_todo.domain.attachment.dto.AttachmentResponse;
import com.todoapp.shared_todo.domain.attachment.dto.PresignedUrlResponse;
import com.todoapp.shared_todo.domain.attachment.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공개 첨부파일 접근 Controller
 * 인증 없이 공유 링크를 통한 접근 허용
 */
@Tag(name = "Public Attachment API", description = "공개 첨부파일 접근 API (인증 불필요)")
@RestController
@RequestMapping("/public/attachments")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class PublicAttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 공개 링크로 첨부파일 조회
     * GET /public/attachments/{shareUuid}
     */
    @Operation(summary = "공개 링크로 첨부파일 조회", description = "공유 링크 UUID를 통해 첨부파일 정보를 조회합니다. (인증 불필요)")
    @GetMapping("/{shareUuid}")
    public ResponseEntity<AttachmentResponse> getAttachmentByShareLink(
            @Parameter(description = "공유 링크 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String shareUuid) {

        AttachmentResponse response = attachmentService.getAttachmentByShareLink(shareUuid);
        return ResponseEntity.ok(response);
    }

    /**
     * 공개 링크로 Presigned URL 생성
     * GET /public/attachments/{shareUuid}/presigned-url
     */
    @Operation(summary = "공개 링크로 Presigned URL 생성", description = "공유 링크 UUID를 통해 다운로드용 Presigned URL을 생성합니다. (인증 불필요)")
    @GetMapping("/{shareUuid}/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedDownloadUrlByShareLink(
            @Parameter(description = "공유 링크 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String shareUuid) {

        PresignedUrlResponse response = attachmentService.generatePresignedDownloadUrlByShareLink(shareUuid);
        return ResponseEntity.ok(response);
    }
}

