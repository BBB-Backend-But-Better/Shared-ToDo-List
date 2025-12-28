package com.todoapp.shared_todo.domain.boardMember.repository;

import com.todoapp.shared_todo.domain.boardMember.entity.BoardMember;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    /**
     * 특정 보드에 속한 모든 멤버 조회
     * - 보드 멤버 목록 API에서 사용
     */
    List<BoardMember> findByBoardId(Long boardId);

    /**
     * 특정 보드에 특정 역할을 가진 멤버 조회
     * - 역할 필터링용
     */
    List<BoardMember> findByBoardIdAndRole(Long boardId, BoardMemberRole role);

    /**
     * 특정 보드에 특정 유저가 멤버로 존재하는지 조회
     * - 권한 체크 (OWNER / GUEST 공통)
     * - 초대 수락 시 중복 참여 방지
     */
    Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);

    /**
     * 특정 보드에 특정 유저가 이미 참여 중인지 여부 확인
     * - 초대 생성 / 수락 전 검증용
     */
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    /**
     * 특정 보드에서 특정 유저를 멤버에서 제거
     * - OWNER가 다른 멤버 강퇴
     * - GUEST 본인 나가기
     */
    void deleteByBoardIdAndUserId(Long boardId, Long userId);
}