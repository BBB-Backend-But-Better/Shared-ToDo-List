package com.todoapp.shared_todo.domain.boardMember.repository;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMember;
import com.todoapp.shared_todo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    // 보드와 유저로 BoardMember 조회
    Optional<BoardMember> findByBoardAndUser(Board board, User user);

    // 보드 ID와 유저 ID로 BoardMember 조회
    Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);

    // 보드의 모든 멤버 조회
    List<BoardMember> findByBoard(Board board);

    // 유저가 참여한 모든 보드 조회
    List<BoardMember> findByUser(User user);

    // 보드 ID로 모든 멤버 조회
    List<BoardMember> findByBoardId(Long boardId);
}

