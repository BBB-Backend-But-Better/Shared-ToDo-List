package com.todoapp.shared_todo.domain.boardMember.controller;

import com.todoapp.shared_todo.domain.boardMember.dto.BoardMemberResponse;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import com.todoapp.shared_todo.domain.boardMember.service.BoardMemberService;
import com.todoapp.shared_todo.global.dto.SimpleStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class BoardMemberController {

    private final BoardMemberService boardMemberService;

    /**
     * 보드 멤버 목록 조회
     */
    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getBoardMembers(
            @PathVariable Long boardId,
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @RequestParam(required = false) BoardMemberRole role) { // 역할 필터 (선택적)
        List<BoardMemberResponse> responses = boardMemberService.getBoardMembers(boardId, userId, role);
        return ResponseEntity.ok(responses);
    }

    /**
     * 보드 멤버 삭제 (OWNER만 가능, 자기 자신 제거 불가)
     */
    @PutMapping("/{boardId}/members/{userId}")
    public ResponseEntity<SimpleStatusResponse> deleteBoardMember(
            @PathVariable Long boardId,
            @PathVariable Long userId, // 삭제할 멤버의 userId
            @RequestParam Long ownerId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        boardMemberService.deleteBoardMember(boardId, ownerId, userId);
        return ResponseEntity.ok(new SimpleStatusResponse(true));
    }

    /**
     * 보드 멤버 나가기 (GUEST만 가능)
     */
    @PutMapping("/{boardId}/members/me/leave")
    public ResponseEntity<SimpleStatusResponse> leaveBoard(
            @PathVariable Long boardId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        boardMemberService.leaveBoard(boardId, userId);
        return ResponseEntity.ok(new SimpleStatusResponse(true));
    }
}

