package com.todoapp.shared_todo.domain.task.service;

import com.todoapp.shared_todo.domain.task.dto.TaskCreateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskResponse;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateStatusRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateStatusRequest;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.task.entity.Task;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.task.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    /**
     * task 생성
     */
    @Transactional
    public TaskResponse createTask(Long boardId, Long userId, TaskCreateRequest requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        Task task = Task.create(requestDto.getContent(), board);
        Task savedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .taskId(savedTask.getTaskId())
                .content(savedTask.getContent())
                .status(savedTask.getStatus())
                .build();
    }

    /**
     * 보드의 task 리스트 조회
     */
    public List<TaskResponse> getTasks(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        List<Task> tasks = taskRepository.findByBoard(board);

        return tasks.stream()
                .map(task -> TaskResponse.builder()
                        .taskId(task.getTaskId())
                        .content(task.getContent())
                        .status(task.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * task 단건 조회
     */
    public TaskResponse getTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = task.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("Task에 접근할 권한이 없습니다.");
        }

        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .content(task.getContent())
                .status(task.getStatus())
                .build();
    }

    /**
     * task 내용 수정
     */
    @Transactional
    public TaskResponse updateTaskContent(Long taskId, Long userId, TaskUpdateRequest requestDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("테Task를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = task.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("Task를 수정할 권한이 없습니다.");
        }

        task.setContent(requestDto.getContent());
        Task updatedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .taskId(updatedTask.getTaskId())
                .content(updatedTask.getContent())
                .status(updatedTask.getStatus())
                .build();
    }

    /**
     * task 상태 변경 (토글)
     */
    @Transactional
    public TaskResponse toggleTaskStatus(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = task.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("Task 상태를 변경할 권한이 없습니다.");
        }

        task.toggleStatus();
        Task updatedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .taskId(updatedTask.getTaskId())
                .content(updatedTask.getContent())
                .status(updatedTask.getStatus())
                .build();
    }

    /**
     * task 상태 변경 (특정 상태로 설정)
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Long userId, @Valid TaskUpdateStatusRequest requestDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = task.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("Task 상태를 변경할 권한이 없습니다.");
        }

        task.setStatus(requestDto.getStatus());
        Task updatedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .taskId(updatedTask.getTaskId())
                .content(updatedTask.getContent())
                .status(updatedTask.getStatus())
                .build();
    }

    /**
     * task 삭제
     */
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = task.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("Task를 삭제할 권한이 없습니다.");
        }

        taskRepository.delete(task);
    }
}

