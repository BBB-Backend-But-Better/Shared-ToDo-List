package com.todoapp.shared_todo.domain.invitation.controller;

import com.todoapp.shared_todo.domain.invitation.dto.InvitationCreateRequest;
import com.todoapp.shared_todo.domain.invitation.dto.InvitationResponse;
import com.todoapp.shared_todo.domain.invitation.service.InvitationService;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Invitation API", description = "초대 관련 API (보드 공유 요청/수락/거절)")
@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * 초대 발송
     * POST /invitation
     * 다른 사용자에게 내 보드를 공유합니다.
     */
    @Operation(summary = "초대 발송", description = "다른 사용자에게 내 보드에 참여하도록 초대를 보냅니다.")
    @PostMapping
    public ResponseEntity<InvitationResponse> sendInvitation(
            @AuthenticationPrincipal CustomePrincipal userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody InvitationCreateRequest request) {
        InvitationResponse response = invitationService.sendInvitation(userId.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 받은 초대 목록 조회
     * GET /invitation
     * 나에게 온 대기 중(PENDING)인 초대 목록을 확인합니다.
     */
    @Operation(summary = "받은 초대 목록 조회", description = "나에게 온 대기 중(PENDING)인 초대 목록을 확인합니다.")
    @GetMapping
    public ResponseEntity<List<InvitationResponse>> getReceivedInvitations(
           @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        List<InvitationResponse> responses = invitationService.getReceivedInvitations(userId.getUserId());
        return ResponseEntity.ok(responses);
    }

    /**
     * 초대 수락
     * POST /invitation/{invitationId}/accept
     * 초대를 수락하여 해당 보드의 멤버(GUEST)로 참여합니다.
     */
    @Operation(summary = "초대 수락", description = "초대를 수락하여 해당 보드의 멤버(GUEST)로 참여합니다.")
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<InvitationResponse> acceptInvitation(
            @Parameter(description = "초대 ID", example = "1")@PathVariable Long invitationId,
           @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        InvitationResponse response = invitationService.acceptInvitation(invitationId, userId.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * 초대 거절
     * POST /invitation/{invitationId}/reject
     * 초대를 거절(REJECTED)합니다.
     */
    @Operation(summary = "초대 거절", description = "초대를 거절(REJECTED)하고 보드에 참여하지 않습니다.")
    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<InvitationResponse> rejectInvitation(
            @Parameter(description = "초대 ID", example = "1") @PathVariable Long invitationId,
           @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        InvitationResponse response = invitationService.rejectInvitation(invitationId, userId.getUserId());
        return ResponseEntity.ok(response);
    }
}
