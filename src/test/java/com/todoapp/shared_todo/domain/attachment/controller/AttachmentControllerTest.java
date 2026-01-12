package com.todoapp.shared_todo.domain.attachment.controller;

import com.todoapp.shared_todo.domain.attachment.dto.*;
import com.todoapp.shared_todo.domain.attachment.service.AttachmentService;
import com.todoapp.shared_todo.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Attachment Controller 단위 테스트
 * MockMvc를 사용한 HTTP 요청/응답 검증
 */
@WebMvcTest(AttachmentController.class)
@DisplayName("Attachment Controller 테스트")
@SuppressWarnings("unused")
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;

    private CustomUserDetails userDetails;

    @Test
    @DisplayName("파일 업로드 성공")
    @WithMockUser
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test content".getBytes()
        );

        AttachmentResponse response = AttachmentResponse.builder()
                .id(1L)
                .originFilename("test.pdf")
                .fileSize(1024L)
                .contentType("application/pdf")
                .uploaderId(1L)
                .uploaderNickname("Test User")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(attachmentService.uploadFile(eq(1L), eq(1L), any()))
                .willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(multipart("/boards/{boardId}/attachments", 1L)
                        .file(file)
                        .with(user(userDetails))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originFilename").value("test.pdf"));
    }

    @Test
    @DisplayName("첨부파일 목록 조회 성공")
    @WithMockUser
    void getAttachments_success() throws Exception {
        AttachmentResponse attachment1 = AttachmentResponse.builder()
                .id(1L)
                .originFilename("file1.pdf")
                .fileSize(1024L)
                .contentType("application/pdf")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        AttachmentResponse attachment2 = AttachmentResponse.builder()
                .id(2L)
                .originFilename("file2.jpg")
                .fileSize(2048L)
                .contentType("image/jpeg")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        AttachmentListResponse response = AttachmentListResponse.builder()
                .attachments(List.of(attachment1, attachment2))
                .totalCount(2L)
                .build();

        given(attachmentService.getAttachments(1L, 1L)).willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(get("/boards/{boardId}/attachments", 1L)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attachments").isArray())
                .andExpect(jsonPath("$.attachments.length()").value(2))
                .andExpect(jsonPath("$.totalCount").value(2));
    }

    @Test
    @DisplayName("첨부파일 단건 조회 성공")
    @WithMockUser
    void getAttachment_success() throws Exception {
        AttachmentResponse response = AttachmentResponse.builder()
                .id(1L)
                .originFilename("test.pdf")
                .fileSize(1024L)
                .contentType("application/pdf")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(attachmentService.getAttachment(1L, 1L, 1L)).willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(get("/boards/{boardId}/attachments/{attachmentId}", 1L, 1L)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originFilename").value("test.pdf"));
    }

    @Test
    @DisplayName("Presigned URL 생성 성공")
    @WithMockUser
    void generatePresignedDownloadUrl_success() throws Exception {
        PresignedUrlResponse response = PresignedUrlResponse.builder()
                .presignedUrl("https://s3.amazonaws.com/presigned-url")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        given(attachmentService.generatePresignedDownloadUrl(1L, 1L, 1L))
                .willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(post("/boards/{boardId}/attachments/{attachmentId}/presigned-url", 1L, 1L)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presignedUrl").exists())
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("AWS S3 공유 링크 생성 성공")
    @WithMockUser
    void createAwsShareLink_success() throws Exception {
        AttachmentShareResponse response = AttachmentShareResponse.builder()
                .shareUuid("550e8400-e29b-41d4-a716-446655440000")
                .shareUrl("http://localhost:8080/api/public/attachments/550e8400-e29b-41d4-a716-446655440000")
                .build();

        given(attachmentService.createAwsShareLink(eq(1L), eq(1L), eq(1L)))
                .willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(post("/boards/{boardId}/attachments/{attachmentId}/share", 1L, 1L)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareUuid").exists())
                .andExpect(jsonPath("$.shareUrl").exists());
    }

    @Test
    @DisplayName("첨부파일 삭제 성공")
    @WithMockUser
    void deleteAttachment_success() throws Exception {
        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(delete("/boards/{boardId}/attachments/{attachmentId}", 1L, 1L)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }
}

