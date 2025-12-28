package com.todoapp.shared_todo.domain.invitation.repository;

import com.todoapp.shared_todo.domain.invitation.entity.Invitation;
import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    /**
     * 받은 초대 목록 조회 (초대 받은 사용자 기준)
     * - 상태(PENDING) 필터
     */
    @Query("""
        select i
        from Invitation i
        join fetch i.board b
        join fetch i.inviter inv
        where i.invitee.id = :inviteeId
          and i.status = :status
        order by i.createdAt desc
    """)
    List<Invitation> findReceivedInvitations(@Param("inviteeId") Long inviteeId, @Param("status") InvitationStatus status);

    /**
     * 특정 보드의 초대 목록 조회 (OWNER 기준)
     */
    @Query("""
        select i
        from Invitation i
        join fetch i.invitee inv
        where i.board.id = :boardId
        order by i.createdAt desc
    """)
    List<Invitation> findByBoardId(@Param("boardId") Long boardId);

    /**
     * 초대 단건 조회 (수락 / 거절 시 권한 검증용)
     */
    Optional<Invitation> findByIdAndInviteeId(Long invitationId, Long inviteeId);

    /**
     * 중복 초대 방지
     * - 동일 보드에 PENDING 상태 초대가 이미 존재하는지 확인
     */
    boolean existsByBoardIdAndInviteeIdAndStatus(Long boardId, Long inviteeId, InvitationStatus status);
}