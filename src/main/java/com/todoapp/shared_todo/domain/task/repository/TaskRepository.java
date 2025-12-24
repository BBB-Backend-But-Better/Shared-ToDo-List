package com.todoapp.shared_todo.domain.task.repository;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 보드별 task 리스트 조회
    List<Task> findByBoard(Board board);

    // 보드 ID로 task 리스트 조회
    List<Task> findByBoard_BoardId(Long boardId);

    // task ID와 보드로 조회 (권한 확인용)
    Optional<Task> findByTaskIdAndBoard(Long taskId, Board board);
}

