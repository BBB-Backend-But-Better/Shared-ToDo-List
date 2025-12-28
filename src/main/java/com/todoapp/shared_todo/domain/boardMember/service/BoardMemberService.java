package com.todoapp.shared_todo.domain.boardMember.service;

import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.boardMember.dto.BoardMemberResponse;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMember;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import com.todoapp.shared_todo.domain.boardMember.repository.BoardMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;

    /**
     * 보드 멤버 목록 조회
     */
    public List<BoardMemberResponse> getBoardMembers(Long boardId, Long userId, BoardMemberRole role) {
        // 보드 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 요청한 사용자가 보드 멤버인지 확인
        boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("보드에 접근할 권한이 없습니다."));

        // 역할 필터링이 있는 경우 해당 역할의 멤버만 조회, 없으면 전체 조회
        List<BoardMember> members;
        if (role != null) {
            members = boardMemberRepository.findByBoardIdAndRole(boardId, role);
        } else {
            members = boardMemberRepository.findByBoardId(boardId);
        }

        return members.stream()
                .map(member -> new BoardMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getNickname(),
                        member.getRole()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 보드 멤버 삭제 (OWNER만 가능, 자기 자신 제거 불가)
     */
    @Transactional
    public void deleteBoardMember(Long boardId, Long ownerId, Long memberUserId) {
        // 보드 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 요청자가 OWNER인지 확인
        BoardMember ownerMember = boardMemberRepository.findByBoardIdAndUserId(boardId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("보드에 접근할 권한이 없습니다."));

        if (ownerMember.getRole() != BoardMemberRole.OWNER) {
            throw new IllegalArgumentException("보드 멤버를 삭제할 권한이 없습니다. OWNER만 가능합니다.");
        }

        // 자기 자신 제거 불가
        if (ownerId.equals(memberUserId)) {
            throw new IllegalArgumentException("자기 자신은 제거할 수 없습니다.");
        }

        // 삭제할 멤버 존재 확인
        if (!boardMemberRepository.existsByBoardIdAndUserId(boardId, memberUserId)) {
            throw new IllegalArgumentException("삭제할 멤버를 찾을 수 없습니다.");
        }

        // 멤버 삭제
        boardMemberRepository.deleteByBoardIdAndUserId(boardId, memberUserId);
    }

    /**
     * 보드 멤버 나가기 (GUEST만 가능)
     */
    @Transactional
    public void leaveBoard(Long boardId, Long userId) {
        // 보드 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 사용자가 보드 멤버인지 확인
        BoardMember member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("보드 멤버를 찾을 수 없습니다."));

        // GUEST만 나가기 가능
        if (member.getRole() != BoardMemberRole.GUEST) {
            throw new IllegalArgumentException("GUEST만 보드를 나갈 수 있습니다.");
        }

        // 멤버 삭제
        boardMemberRepository.deleteByBoardIdAndUserId(boardId, userId);
    }
}

