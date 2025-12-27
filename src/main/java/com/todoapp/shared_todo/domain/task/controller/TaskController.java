package com.todoapp.shared_todo.domain.task.controller;

import com.todoapp.shared_todo.domain.task.dto.TaskCreateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskResponse;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateStatusRequest;
import com.todoapp.shared_todo.domain.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/tasks")
@RequiredArgsConstructor
@SuppressWarnings("unused") // Spring이 런타임에 자동으로 등록하고 사용하는 Controller
public class TaskController {

    private final TaskService taskService;

    /**
     * Task 생성
     * POST /boards/{boardId}/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long boardId,
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(boardId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드의 Task 리스트 조회
     * GET /boards/{boardId}/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long boardId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        List<TaskResponse> responses = taskService.getTasks(boardId, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Task 단건 조회
     * GET /boards/{boardId}/tasks/{taskId}
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        TaskResponse response = taskService.getTask(boardId, taskId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Task 내용 수정
     * PUT /boards/{boardId}/tasks/{taskId}
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(boardId, taskId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Task 상태 변경 (토글)
     * PATCH /boards/{boardId}/tasks/{taskId}/toggle
     */
    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<TaskResponse> toggleTaskStatus(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        TaskResponse response = taskService.toggleTaskStatus(boardId, taskId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Task 상태 변경 (특정 상태로 설정)
     * PATCH /boards/{boardId}/tasks/{taskId}/status
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam Long userId, // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
            @Valid @RequestBody TaskUpdateStatusRequest request) {
        TaskResponse response = taskService.updateTaskStatus(boardId, taskId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Task 삭제
     * DELETE /boards/{boardId}/tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam Long userId) { // TODO: JWT 인증 후 SecurityContext에서 가져오도록 변경
        taskService.deleteTask(boardId, taskId, userId);
        return ResponseEntity.noContent().build();
    }
}
