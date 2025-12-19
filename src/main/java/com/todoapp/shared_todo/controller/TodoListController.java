package com.todoapp.shared_todo.controller;

import com.todoapp.shared_todo.dto.todoLists.TodoCreateRequestDto;
import com.todoapp.shared_todo.dto.todoLists.TodoResponseDto;
import com.todoapp.shared_todo.dto.todoLists.TodoUpdateRequestDto;
import com.todoapp.shared_todo.dto.todoLists.TodoUpdateStatusRequestDto;
import com.todoapp.shared_todo.service.TodoListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/todos")
@RequiredArgsConstructor
public class TodoListController {

    private final TodoListService todoListService;

    /**
     * todo 생성
     * POST /api/boards/{boardId}/todos
     */
    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TodoCreateRequestDto requestDto) {
        TodoResponseDto response = todoListService.createTodo(boardId, userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드의 todo 목록 조회
     * GET /api/boards/{boardId}/todos
     */
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getTodos(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId) {
        List<TodoResponseDto> todos = todoListService.getTodos(boardId, userId);
        return ResponseEntity.ok(todos);
    }

    /**
     * todo 단건 조회
     * GET /api/boards/{boardId}/todos/{todoId}
     */
    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponseDto> getTodo(
            @PathVariable Long todoId,
            @RequestHeader("X-User-Id") Long userId) {
        TodoResponseDto todo = todoListService.getTodo(todoId, userId);
        return ResponseEntity.ok(todo);
    }

    /**
     * todo 내용 수정
     * PATCH /api/boards/{boardId}/todos/{todoId}/content
     */
    @PatchMapping("/{todoId}/content")
    public ResponseEntity<TodoResponseDto> updateTodoContent(
            @PathVariable Long todoId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TodoUpdateRequestDto requestDto) {
        TodoResponseDto response = todoListService.updateTodoContent(todoId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * todo 상태 토글 (UNCHECKED <-> CHECKED)
     * PATCH /api/boards/{boardId}/todos/{todoId}/status/toggle
     */
    @PatchMapping("/{todoId}/status/toggle")
    public ResponseEntity<TodoResponseDto> toggleTodoStatus(
            @PathVariable Long todoId,
            @RequestHeader("X-User-Id") Long userId) {
        TodoResponseDto response = todoListService.toggleTodoStatus(todoId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * todo 상태 변경 (특정 상태로 설정)
     * PATCH /api/boards/{boardId}/todos/{todoId}/status
     */
    @PatchMapping("/{todoId}/status")
    public ResponseEntity<TodoResponseDto> updateTodoStatus(
            @PathVariable Long todoId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TodoUpdateStatusRequestDto requestDto) {
        TodoResponseDto response = todoListService.updateTodoStatus(todoId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * todo 삭제
     * DELETE /api/boards/{boardId}/todos/{todoId}
     */
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long todoId,
            @RequestHeader("X-User-Id") Long userId) {
        todoListService.deleteTodo(todoId, userId);
        return ResponseEntity.noContent().build();
    }
}

