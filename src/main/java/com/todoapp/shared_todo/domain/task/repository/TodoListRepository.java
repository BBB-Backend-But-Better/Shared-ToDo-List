package com.todoapp.shared_todo.domain.task.repository;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.task.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    // 보드별 todo 리스트 조회
    List<TodoList> findByBoard(Board board);

    // 보드 ID로 todo 리스트 조회
    List<TodoList> findByBoard_BoardId(Long boardId);

    // todo ID와 보드로 조회 (권한 확인용)
    Optional<TodoList> findByTodoIdAndBoard(Long todoId, Board board);
}

