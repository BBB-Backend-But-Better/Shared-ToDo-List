package com.todoapp.shared_todo.domain.board.controller;

import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequest;
import com.todoapp.shared_todo.domain.board.dto.BoardResponse;
import com.todoapp.shared_todo.domain.board.dto.BoardUpdateTitleRequest;
import com.todoapp.shared_todo.domain.board.service.BoardService;
import com.todoapp.shared_todo.global.dto.ApiResponse;
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

@Tag(name = "Board API", description = "보드(공유의 대상) 관련 API")
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
    @Operation(summary = "게시글 작성", description = "로그인한 유저가 새로운 게시글을 생성합니다.")
    @PostMapping
    public ApiResponse<BoardResponse> createBoard(
            @AuthenticationPrincipal CustomePrincipal userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.createBoard(userId.getUserId(), request);
        return ApiResponse.onSuccess(response);
    }

    /**
     * 보드 목록 조회
     * GET /boards
     */
    @Operation(summary = "게시글 목록(전체) 조회", description = "로그인한 유저가 게시글을 전체 조회합니다.")
    @GetMapping
    public ApiResponse<List<BoardResponse>> getBoards(
            @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        List<BoardResponse> responses = boardService.getBoards(userId.getUserId());
        return ApiResponse.onSuccess(responses);
    }

    /**
     * 보드 단건 조회
     * GET /boards/{boardId}
     */
    @Operation(summary = "보드 단건 조회", description = "특정 보드의 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoard(
            @Parameter(description = "조회할 보드의 ID", example = "1") @PathVariable Long boardId,
            @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        BoardResponse response = boardService.getBoard(boardId, userId.getUserId());
        return ApiResponse.onSuccess(response);
    }

    /**
     * 보드 제목 수정
     * PUT /boards/{boardId}/title
     */
    @Operation(summary = "보드 제목 수정", description = "특정 보드의 제목을 수정합니다.")
    @PutMapping("/{boardId}/title")
    public ApiResponse<BoardResponse> updateBoardTitle(
            @Parameter(description = "수정할 보드의 ID", example = "1") @PathVariable Long boardId,
            @AuthenticationPrincipal CustomePrincipal userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody BoardUpdateTitleRequest request) {
        BoardResponse response = boardService.updateBoardTitle(boardId, userId.getUserId(), request.getTitle());
        return ApiResponse.onSuccess(response);
    }

    /**
     * 보드 삭제
     * DELETE /boards/{boardId}
     */
    @Operation(summary = "보드 삭제", description = "특정 보드를 삭제합니다. (소유자만 가능)")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBoard(
            @Parameter(description = "삭제할 보드의 ID", example = "1") @PathVariable Long boardId,
            @AuthenticationPrincipal CustomePrincipal userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        boardService.deleteBoard(boardId, userId.getUserId());
        return ApiResponse.onSuccess(null);
    }
}
