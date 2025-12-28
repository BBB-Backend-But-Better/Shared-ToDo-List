package com.todoapp.shared_todo.domain.invitation.service;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMember;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import com.todoapp.shared_todo.domain.boardMember.repository.BoardMemberRepository;
import com.todoapp.shared_todo.domain.invitation.dto.InvitationCreateRequest;
import com.todoapp.shared_todo.domain.invitation.dto.InvitationResponse;
import com.todoapp.shared_todo.domain.invitation.entity.Invitation;
import com.todoapp.shared_todo.domain.invitation.entity.InvitationStatus;
import com.todoapp.shared_todo.domain.invitation.repository.InvitationRepository;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;
    private final BoardMemberRepository boardMemberRepository;

    /**
     * 초대 발송
     * POST /invitation
     * 다른 사용자에게 내 보드를 공유합니다.
     */
    @Transactional
    public InvitationResponse sendInvitation(Long inviterId, InvitationCreateRequest request) {
        // 초대하는 사용자 조회
        User inviter = usersRepository.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 보드 조회 및 소유자 확인
        Board board = boardRepository.findByIdAndAuthor(request.boardId(), inviter)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없거나 접근 권한이 없습니다."));

        // 초대받을 사용자 조회 (userCode로 찾기)
        User invitee = usersRepository.findByUserCode(request.userCode())
                .orElseThrow(() -> new IllegalArgumentException("초대할 사용자를 찾을 수 없습니다."));

        // 자기 자신에게 초대 불가
        if (inviter.getId().equals(invitee.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 초대할 수 없습니다.");
        }

        // 중복 초대 방지 (동일 보드에 PENDING 상태 초대가 이미 존재하는지 확인)
        if (invitationRepository.existsByBoardIdAndInviteeIdAndStatus(
                request.boardId(), invitee.getId(), InvitationStatus.PENDING)) {
            throw new IllegalStateException("이미 대기 중인 초대가 존재합니다.");
        }

        // 만료 시간 설정 (7일 후)
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // 초대 생성 및 저장
        Invitation invitation = Invitation.create(board, inviter, invitee, expiresAt);
        Invitation savedInvitation = invitationRepository.save(invitation);

        return toResponse(savedInvitation);
    }

    /**
     * 받은 초대 목록 조회
     * GET /invitation
     * 나에게 온 대기 중(PENDING)인 초대 목록을 확인합니다.
     */
    public List<InvitationResponse> getReceivedInvitations(Long userId) {
        // 사용자 조회
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // PENDING 상태의 받은 초대 목록 조회
        List<Invitation> invitations = invitationRepository.findReceivedInvitations(
                user.getId(), InvitationStatus.PENDING);

        return invitations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 초대 수락
     * POST /invitation/{invitationId}/accept
     * 초대를 수락하여 해당 보드의 멤버(GUEST)로 참여합니다.
     */
    @Transactional
    public InvitationResponse acceptInvitation(Long invitationId, Long userId) {
        // 초대 조회 및 권한 확인 (초대받은 사용자만 수락 가능)
        Invitation invitation = invitationRepository.findByIdAndInviteeId(invitationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없거나 접근 권한이 없습니다."));

        // 초대 수락 (도메인 로직 실행)
        invitation.accept();
        Invitation savedInvitation = invitationRepository.save(invitation);

        // 보드 멤버로 추가 (GUEST 역할)
        Board board = invitation.getBoard();
        User invitee = invitation.getInvitee();
        
        // 중복 참여 방지 (이미 멤버인 경우 건너뜀)
        if (!boardMemberRepository.existsByBoardIdAndUserId(board.getId(), invitee.getId())) {
            BoardMember boardMember = BoardMember.create(board, invitee, BoardMemberRole.GUEST);
            boardMemberRepository.save(boardMember);
        }

        return toResponse(savedInvitation);
    }

    /**
     * 초대 거절
     * POST /invitation/{invitationId}/reject
     * 초대를 거절(REJECTED)합니다.
     */
    @Transactional
    public InvitationResponse rejectInvitation(Long invitationId, Long userId) {
        // 초대 조회 및 권한 확인 (초대받은 사용자만 거절 가능)
        Invitation invitation = invitationRepository.findByIdAndInviteeId(invitationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없거나 접근 권한이 없습니다."));

        // 초대 거절 (도메인 로직 실행)
        invitation.reject();
        Invitation savedInvitation = invitationRepository.save(invitation);

        return toResponse(savedInvitation);
    }

    /**
     * Invitation 엔티티를 InvitationResponse DTO로 변환
     */
    private InvitationResponse toResponse(Invitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getBoard().getId(),
                invitation.getBoard().getTitle(),
                invitation.getInviter().getNickname(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getExpiresAt()
        );
    }
}
