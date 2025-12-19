package com.todoapp.shared_todo.controller;

import com.todoapp.shared_todo.dto.boards.BoardCreateRequestDto;
import com.todoapp.shared_todo.dto.boards.BoardResponseDto;
import com.todoapp.shared_todo.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 보드 생성
     * POST /api/boards
     * 
     * TODO: 태주님과 협의 필요
     * - Users 객체를 조회하는 방법 (UsersService 사용 또는 다른 방법)
     * - 현재는 userId만 받고 있으므로, Users 조회 로직 추가 필요
     */
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BoardCreateRequestDto requestDto) {
        // TODO: Users 도메인 담당자가 만든 서비스를 통해 Users 객체 조회
        // Users owner = usersService.findById(userId);
        // BoardResponseDto response = boardService.createBoard(owner, requestDto);
        
        // 임시로 예외 발생 (실제 구현 시 위 주석 코드로 교체)
        throw new UnsupportedOperationException(
                "Users 도메인 담당자와 협의하여 Users 조회 로직을 추가해야 합니다.");
    }

    /**
     * 보드 목록 조회 (사용자가 접근 가능한 보드)
     * GET /api/boards
     */
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoards(
            @RequestHeader("X-User-Id") Long userId) {
        List<BoardResponseDto> boards = boardService.getBoards(userId);
        return ResponseEntity.ok(boards);
    }

    /**
     * 보드 단건 조회
     * GET /api/boards/{boardId}
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId) {
        BoardResponseDto board = boardService.getBoard(boardId, userId);
        return ResponseEntity.ok(board);
    }

    /**
     * 보드 제목 수정
     * PATCH /api/boards/{boardId}/title
     */
    @PatchMapping("/{boardId}/title")
    public ResponseEntity<BoardResponseDto> updateBoardTitle(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody String newTitle) {
        BoardResponseDto response = boardService.updateBoardTitle(boardId, userId, newTitle);
        return ResponseEntity.ok(response);
    }

    /**
     * 보드 삭제
     * DELETE /api/boards/{boardId}
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.noContent().build();
    }
}

