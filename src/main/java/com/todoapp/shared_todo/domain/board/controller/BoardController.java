package com.todoapp.shared_todo.domain.board.controller;

import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequest;
import com.todoapp.shared_todo.domain.board.dto.BoardResponse;
import com.todoapp.shared_todo.domain.board.dto.BoardUpdateTitleRequest;
import com.todoapp.shared_todo.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class BoardController {

    private final BoardService boardService;

    /**
     * 보드 생성
     * POST /boards
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.createBoard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드 목록 조회
     * GET /boards
     */
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards(
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        List<BoardResponse> responses = boardService.getBoards(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 보드 단건 조회
     * GET /boards/{boardId}
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(
            @PathVariable Long boardId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        BoardResponse response = boardService.getBoard(boardId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 보드 제목 수정
     * PUT /boards/{boardId}/title
     */
    @PutMapping("/{boardId}/title")
    public ResponseEntity<BoardResponse> updateBoardTitle(
            @PathVariable Long boardId,
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody BoardUpdateTitleRequest request) {
        BoardResponse response = boardService.updateBoardTitle(boardId, userId, request.getTitle());
        return ResponseEntity.ok(response);
    }

    /**
     * 보드 삭제
     * DELETE /boards/{boardId}
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.noContent().build();
    }
}
