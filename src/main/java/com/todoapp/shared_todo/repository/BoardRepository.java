package com.todoapp.shared_todo.repository;

import com.todoapp.shared_todo.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 보드 ID와 소유자 ID로 조회 (권한 확인용)
    @Query("SELECT b FROM Board b WHERE b.boardId = :boardId AND b.owner.id = :ownerId")
    Optional<Board> findByBoardIdAndOwnerId(@Param("boardId") Long boardId, @Param("ownerId") Long ownerId);

    // 소유자이거나 공유된 사용자가 접근 가능한 보드 목록 조회
    // @Query를 사용하여 명확하게 쿼리 작성
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN b.sharedUsers su " +
           "WHERE b.owner.id = :userId OR su.id = :userId")
    List<Board> findAccessibleBoardsByUserId(@Param("userId") Long userId);
}

