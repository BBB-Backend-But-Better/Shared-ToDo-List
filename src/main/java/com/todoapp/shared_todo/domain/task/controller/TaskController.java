package com.todoapp.shared_todo.domain.task.controller;

import com.todoapp.shared_todo.domain.task.dto.*;
import com.todoapp.shared_todo.domain.task.dto.TaskCreateRequest;
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
public class TaskController {

    private final TaskService taskService;

    /**
     * task 생성
     * POST /boards/{boardId}/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TaskCreateRequest requestDto) {
        TaskResponse response = taskService.createTask(boardId, userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드의 task 목록 조회
     * GET /boards/{boardId}/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long boardId,
            @RequestHeader("X-User-Id") Long userId) {
        List<TaskResponse> tasks = taskService.getTasks(boardId, userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * task 단건 조회
     * GET /boards/{boardId}/tasks/{taskId}
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId) {
        TaskResponse task = taskService.getTask(taskId, userId);
        return ResponseEntity.ok(task);
    }

    /**
     * task 내용 수정
     * PATCH /boards/{boardId}/tasks/{taskId}/content
     */
    @PatchMapping("/{taskId}/content")
    public ResponseEntity<TaskResponse> updateTaskContent(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TaskUpdateRequest requestDto) {
        TaskResponse response = taskService.updateTaskContent(taskId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * task 상태 토글 (UNCHECKED <-> CHECKED)
     * PATCH /boards/{boardId}/tasks/{taskId}/status/toggle
     */
    @PatchMapping("/{taskId}/status/toggle")
    public ResponseEntity<TaskResponse> toggleTaskStatus(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId) {
        TaskResponse response = taskService.toggleTaskStatus(taskId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * task 상태 변경 (특정 상태로 설정)
     * PATCH /boards/{boardId}/tasks/{taskId}/status
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TaskUpdateStatusRequest requestDto) {
        TaskResponse response = taskService.updateTaskStatus(taskId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * task 삭제
     * DELETE /boards/{boardId}/tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId) {
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}

