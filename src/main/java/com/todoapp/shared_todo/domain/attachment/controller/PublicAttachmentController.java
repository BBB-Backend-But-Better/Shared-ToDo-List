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

@Tag(name = "Public Attachment API", description = "공개 첨부파일 접근 API (인증 불필요)")
@RestController
@RequestMapping("/public/attachments")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class PublicAttachmentController {

    private final AttachmentService attachmentService;

    /**
     * AWS S3 공유 링크로 첨부파일 조회 (인증 불필요)
     * GET /public/attachments/{awsShareUuid}
     * 공유 링크 UUID를 통해 S3에 저장된 첨부파일의 메타데이터를 조회합니다.
     */
    @Operation(summary = "AWS S3 공유 링크로 첨부파일 조회", description = "공유 링크를 통해 S3에 저장된 첨부파일 정보를 조회합니다.")
    @GetMapping("/{awsShareUuid}")
    public ResponseEntity<AttachmentResponse> getAttachmentByAwsShareLink(
            @Parameter(description = "AWS S3 공유 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String awsShareUuid) {

        AttachmentResponse response = attachmentService.getAttachmentByAwsShareLink(awsShareUuid);
        return ResponseEntity.ok(response);
    }

    /**
     * AWS S3 공유 링크로 Presigned URL 생성 (인증 불필요)
     * POST /public/attachments/{awsShareUuid}/presigned-url
     * 공유 링크 UUID를 통해 S3 파일 다운로드를 위한 Presigned URL을 생성합니다.
     */
    @Operation(summary = "AWS S3 공유 링크로 Presigned URL 생성", description = "공유 링크를 통해 S3 파일 다운로드를 위한 Presigned URL을 생성합니다.")
    @PostMapping("/{awsShareUuid}/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generateAwsPresignedDownloadUrlByShareLink(
            @Parameter(description = "AWS S3 공유 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String awsShareUuid) {

        PresignedUrlResponse response = attachmentService.generateAwsPresignedDownloadUrlByShareLink(awsShareUuid);
        return ResponseEntity.ok(response);
    }
}

