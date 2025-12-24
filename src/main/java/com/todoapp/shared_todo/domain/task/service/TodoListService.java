package com.todoapp.shared_todo.domain.task.service;

import com.todoapp.shared_todo.domain.task.dto.TodoCreateRequestDto;
import com.todoapp.shared_todo.domain.task.dto.TodoResponseDto;
import com.todoapp.shared_todo.domain.task.dto.TodoUpdateRequestDto;
import com.todoapp.shared_todo.domain.task.dto.TodoUpdateStatusRequestDto;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.task.entity.TodoList;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.task.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final BoardRepository boardRepository;

    /**
     * todo 생성
     */
    @Transactional
    public TodoResponseDto createTodo(Long boardId, Long userId, TodoCreateRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        TodoList todoList = TodoList.create(requestDto.getContent(), board, userId);
        TodoList savedTodo = todoListRepository.save(todoList);

        return TodoResponseDto.builder()
                .todoId(savedTodo.getTodoId())
                .content(savedTodo.getContent())
                .status(savedTodo.getStatus())
                .build();
    }

    /**
     * 보드의 todo 리스트 조회
     */
    public List<TodoResponseDto> getTodos(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        List<TodoList> todos = todoListRepository.findByBoard(board);

        return todos.stream()
                .map(todo -> TodoResponseDto.builder()
                        .todoId(todo.getTodoId())
                        .content(todo.getContent())
                        .status(todo.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * todo 단건 조회
     */
    public TodoResponseDto getTodo(Long todoId, Long userId) {
        TodoList todo = todoListRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("투두를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = todo.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("투두에 접근할 권한이 없습니다.");
        }

        return TodoResponseDto.builder()
                .todoId(todo.getTodoId())
                .content(todo.getContent())
                .status(todo.getStatus())
                .build();
    }

    /**
     * todo 내용 수정
     */
    @Transactional
    public TodoResponseDto updateTodoContent(Long todoId, Long userId, TodoUpdateRequestDto requestDto) {
        TodoList todo = todoListRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("투두를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = todo.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("투두를 수정할 권한이 없습니다.");
        }

        todo.setContent(requestDto.getContent());
        TodoList updatedTodo = todoListRepository.save(todo);

        return TodoResponseDto.builder()
                .todoId(updatedTodo.getTodoId())
                .content(updatedTodo.getContent())
                .status(updatedTodo.getStatus())
                .build();
    }

    /**
     * todo 상태 변경 (토글)
     */
    @Transactional
    public TodoResponseDto toggleTodoStatus(Long todoId, Long userId) {
        TodoList todo = todoListRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("투두를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = todo.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("투두 상태를 변경할 권한이 없습니다.");
        }

        todo.toggleStatus();
        TodoList updatedTodo = todoListRepository.save(todo);

        return TodoResponseDto.builder()
                .todoId(updatedTodo.getTodoId())
                .content(updatedTodo.getContent())
                .status(updatedTodo.getStatus())
                .build();
    }

    /**
     * todo 상태 변경 (특정 상태로 설정)
     */
    @Transactional
    public TodoResponseDto updateTodoStatus(Long todoId, Long userId, TodoUpdateStatusRequestDto requestDto) {
        TodoList todo = todoListRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("투두를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = todo.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("투두 상태를 변경할 권한이 없습니다.");
        }

        todo.setStatus(requestDto.getStatus());
        TodoList updatedTodo = todoListRepository.save(todo);

        return TodoResponseDto.builder()
                .todoId(updatedTodo.getTodoId())
                .content(updatedTodo.getContent())
                .status(updatedTodo.getStatus())
                .build();
    }

    /**
     * todo 삭제
     */
    @Transactional
    public void deleteTodo(Long todoId, Long userId) {
        TodoList todo = todoListRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("투두를 찾을 수 없습니다."));

        // 보드 접근 권한 확인
        Board board = todo.getBoard();
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("투두를 삭제할 권한이 없습니다.");
        }

        todoListRepository.delete(todo);
    }
}

