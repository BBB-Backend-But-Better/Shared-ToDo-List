package com.todoapp.shared_todo.domain.boardMember.controller;

import com.todoapp.shared_todo.domain.boardMember.dto.BoardMemberResponse;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import com.todoapp.shared_todo.domain.boardMember.service.BoardMemberService;
import com.todoapp.shared_todo.global.dto.SimpleStatusResponse;
import com.todoapp.shared_todo.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Board Member API", description = "보드 멤버 관리 API (초대, 강퇴, 나가기)")
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class BoardMemberController {

    private final BoardMemberService boardMemberService;

    /**
     * 보드 멤버 목록 조회
     */
    @Operation(summary = "보드 멤버 목록 조회", description = "특정 보드에 참여 중인 멤버 목록을 조회합니다.")
    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getBoardMembers(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "역할 필터 (OWNER, GUEST)", example = "GUEST") @RequestParam(required = false) BoardMemberRole role) {

        List<BoardMemberResponse> responses = boardMemberService.getBoardMembers(boardId, userDetails.getUserId(), role);
        return ResponseEntity.ok(responses);
    }

    /**
     * 보드 멤버 삭제 (OWNER만 가능, 자기 자신 제거 불가)
     */
    @Operation(summary = "보드 멤버 삭제", description = "보드 소유자(OWNER)가 특정 멤버를 삭제합니다..")
    @PutMapping("/{boardId}/members/{userId}")
    public ResponseEntity<SimpleStatusResponse> deleteBoardMember(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userId, // 삭제할 멤버의 userId
            @Parameter(description = "삭제할 타겟 멤버의 ID (User PK)", example = "5") @RequestParam Long ownerId) {
        boardMemberService.deleteBoardMember(boardId, ownerId, userId.getUserId());
        return ResponseEntity.ok(new SimpleStatusResponse(true));
    }

    /**
     * 보드 멤버 나가기 (GUEST만 가능)
     */
    @Operation(summary = "보드 나가기", description = "로그인한 유저가 스스로 보드에서 나갑니다.")
    @PutMapping("/{boardId}/members/me/leave")
    public ResponseEntity<SimpleStatusResponse> leaveBoard(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        boardMemberService.leaveBoard(boardId, userId.getUserId());
        return ResponseEntity.ok(new SimpleStatusResponse(true));
    }
}

