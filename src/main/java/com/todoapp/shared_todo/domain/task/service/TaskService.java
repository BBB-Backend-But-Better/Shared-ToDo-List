package com.todoapp.shared_todo.domain.task.service;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.boardMember.repository.BoardMemberRepository;
import com.todoapp.shared_todo.domain.task.dto.TaskCreateRequest;
import com.todoapp.shared_todo.domain.task.dto.TaskResponse;
import com.todoapp.shared_todo.domain.task.dto.TaskUpdateRequest;
import com.todoapp.shared_todo.domain.task.entity.Task;
import com.todoapp.shared_todo.domain.task.repository.TaskRepository;

import jakarta.persistence.OptimisticLockException;
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
    private final BoardMemberRepository boardMemberRepository;

    /**
     * Task 생성
     * 요구사항: Task 생성 시 보드 접근 권한 확인 (소유자)
     */
    @Transactional
    public TaskResponse createTask(Long boardId, Long userId, TaskCreateRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자만 접근 가능
        if (!board.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        Task task = Task.create(request.getDescription(), board, request.getDueDate());
        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    /**
     * 보드의 Task 리스트 조회
     * 요구사항: Task 목록 조회 시 보드 접근 권한 확인
     */
    public List<TaskResponse> getTasks(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자만 접근 가능
        boolean exists = boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
        // 권한 확인: 소유자만 접근 가능
        if (!exists) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        List<Task> tasks = taskRepository.findByBoardId(boardId);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Task 단건 조회
     * 요구사항: Task 단건 조회 시 보드 접근 권한 확인
     */
    public TaskResponse getTask(Long boardId, Long taskId, Long userId) {
        Task task = validateTaskAndBoardAccess(boardId, taskId, userId);

        return TaskResponse.from(task);
    }

    /**
     * Task 내용 수정
     * 요구사항: Task 내용 수정 시 보드 접근 권한 확인
     */
    @Transactional
    public TaskResponse updateTask(Long boardId, Long taskId, Long userId, TaskUpdateRequest request) {
        Task task = validateTaskAndBoardAccess(boardId, taskId, userId);

        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        Task updatedTask = taskRepository.save(task);

        return TaskResponse.from(updatedTask);
    }

    /**
     * Task 상태 변경 (토글)
     * 요구사항: Task 상태 변경 - 토글 방식
     */
    @Transactional
    public TaskResponse toggleTaskStatus(Long boardId, Long taskId, Long userId, Long requestVersion) {
        Task task = validateTaskAndBoardAccess(boardId, taskId, userId);

        if(!task.getVersion().equals(requestVersion)) {
            throw new OptimisticLockException("Task 버전이 충돌하였습니다.");
        }

        task.toggleStatus();

        // flush 시 JPA가 version으로 최종 검증 → 영속 상태이므로 트랜잭션 종료 시 flush, save() 호출이 없어도 됨
        return TaskResponse.from(task);
    }

    /**
     * Task 삭제
     * 요구사항: Task 삭제 시 보드 접근 권한 확인
     */
    @Transactional
    public void deleteTask(Long boardId, Long taskId, Long userId) {
        Task task = validateTaskAndBoardAccess(boardId, taskId, userId);
        taskRepository.delete(task);
    }

    /**
     * Task 조회 및 boardId 검증, 보드 접근 권한 확인
     * 여러 메서드에서 공통으로 사용되는 검증 로직을 추출
     */
    private Task validateTaskAndBoardAccess(Long boardId, Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task를 찾을 수 없습니다."));

        // boardId 검증: Task가 해당 보드에 속하는지 확인
        if (!task.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("Task가 해당 보드에 속하지 않습니다.");
        }

        // 보드 접근 권한 확인: 소유자만 접근 가능
        if (!task.getBoard().getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        return task;
    }
}