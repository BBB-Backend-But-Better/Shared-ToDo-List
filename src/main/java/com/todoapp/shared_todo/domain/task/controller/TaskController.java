package com.todoapp.shared_todo.domain.task.controller;

import com.todoapp.shared_todo.domain.task.dto.TaskCheckRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskCreateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskResponse;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateRequest;
import com.todoapp.shared_todo.domain.task.service.TaskService;
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

@Tag(name = "Task API", description = "보드 내부의 할 일(Task) 관리 API")
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
    @Operation(summary = "Task 생성", description = "특정 보드 내에 새로운 할 일을 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails,
            @Valid @RequestBody TaskCreateRequest request) {

        TaskResponse response = taskService.createTask(boardId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 보드의 Task 리스트 조회
     * GET /boards/{boardId}/tasks
     */
    @Operation(summary = "Task 목록 조회", description = "특정 보드에 포함된 모든 할 일 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails) {

        List<TaskResponse> responses = taskService.getTasks(boardId, userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    /**
     * Task 단건 조회
     * GET /boards/{boardId}/tasks/{taskId}
     */
    @Operation(summary = "Task 단건 조회", description = "특정 할 일의 상세 정보를 조회합니다.")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "할 일 ID", example = "10") @PathVariable Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails) {

        TaskResponse response = taskService.getTask(boardId, taskId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Task 내용 수정", description = "할 일의 제목, 내용, 담당자 등을 수정합니다.")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "할 일 ID", example = "10") @PathVariable Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails,
            @Valid @RequestBody TaskUpdateRequest request) {

        TaskResponse response = taskService.updateTask(boardId, taskId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Task 상태 변경 (토글)
     */
    @Operation(summary = "Task 완료 여부 토글", description = "할 일의 완료 상태(UNCHECK, CHECK)를 반전시킵니다.")
    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<TaskResponse> toggleTaskStatus(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "할 일 ID", example = "10") @PathVariable Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails,
            @Valid @RequestBody TaskCheckRequest request) {

        TaskResponse response = taskService.toggleTaskStatus(boardId, taskId, userDetails.getUserId(), request.version());
        return ResponseEntity.ok(response);
    }

    /**
     * Task 삭제
     */
    @Operation(summary = "Task 삭제", description = "특정 할 일을 삭제합니다.")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "보드 ID", example = "1") @PathVariable Long boardId,
            @Parameter(description = "할 일 ID", example = "10") @PathVariable Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomePrincipal userDetails) {

        taskService.deleteTask(boardId, taskId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}

