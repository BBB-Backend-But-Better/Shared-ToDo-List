package com.todoapp.shared_todo.domain.board.repository;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 보드 ID와 소유자로 조회
    Optional<Board> findByIdAndAuthor(Long id, User author);

    // 사용자가 소유한 보드 목록 조회
    List<Board> findByAuthor(User author);
}

