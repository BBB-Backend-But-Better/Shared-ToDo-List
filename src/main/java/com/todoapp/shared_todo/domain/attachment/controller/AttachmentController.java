package com.todoapp.shared_todo.domain.attachment.controller;

import com.todoapp.shared_todo.domain.attachment.dto.*;
import com.todoapp.shared_todo.domain.attachment.service.AttachmentService;
import com.todoapp.shared_todo.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachment API", description = "첨부파일 관리 API")
@RestController
@RequestMapping("/boards/{boardId}/attachments")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 파일 업로드
     * POST /boards/{boardId}/attachments
     */
    @Operation(summary = "파일 업로드", description = "보드에 파일을 업로드합니다.")
    @PostMapping
    public ResponseEntity<AttachmentResponse> uploadFile(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        AttachmentResponse response = attachmentService.uploadFile(boardId, userDetails.getUserId(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드별 첨부파일 목록 조회
     * GET /boards/{boardId}/attachments
     */
    @Operation(summary = "첨부파일 목록 조회", description = "보드에 속한 모든 첨부파일 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<AttachmentListResponse> getAttachments(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        AttachmentListResponse response = attachmentService.getAttachments(boardId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * 첨부파일 단건 조회
     * GET /boards/{boardId}/attachments/{attachmentId}
     */
    @Operation(summary = "첨부파일 단건 조회", description = "특정 첨부파일의 정보를 조회합니다.")
    @GetMapping("/{attachmentId}")
    public ResponseEntity<AttachmentResponse> getAttachment(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "첨부파일 ID", example = "10") @PathVariable Long attachmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        AttachmentResponse response = attachmentService.getAttachment(boardId, attachmentId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Presigned URL 생성 (다운로드용)
     * GET /boards/{boardId}/attachments/{attachmentId}/presigned-url
     */
    @Operation(summary = "Presigned URL 생성", description = "첨부파일 다운로드를 위한 Presigned URL을 생성합니다.")
    @GetMapping("/{attachmentId}/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedDownloadUrl(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "첨부파일 ID", example = "10") @PathVariable Long attachmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        PresignedUrlResponse response = attachmentService.generatePresignedDownloadUrl(boardId, attachmentId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * 공유 링크 생성
     * POST /boards/{boardId}/attachments/{attachmentId}/share
     */
    @Operation(summary = "공유 링크 생성", description = "첨부파일 공유를 위한 UUID 기반 링크를 생성합니다.")
    @PostMapping("/{attachmentId}/share")
    public ResponseEntity<AttachmentShareResponse> createShareLink(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "첨부파일 ID", example = "10") @PathVariable Long attachmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AttachmentShareRequest request) {

        AttachmentShareResponse response = attachmentService.createShareLink(boardId, attachmentId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 첨부파일 삭제
     * DELETE /boards/{boardId}/attachments/{attachmentId}
     */
    @Operation(summary = "첨부파일 삭제", description = "첨부파일을 논리 삭제합니다.")
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "첨부파일 ID", example = "10") @PathVariable Long attachmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        attachmentService.deleteAttachment(boardId, attachmentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}

