package com.todoapp.shared_todo.domain.board.service;

import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequestDto;
import com.todoapp.shared_todo.domain.board.dto.BoardResponseDto;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.Users;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 보드 생성
     * @param owner Users 객체 (Controller나 상위 레이어에서 조회하여 전달)
     */
    @Transactional
    public BoardResponseDto createBoard(Users owner, BoardCreateRequestDto requestDto) {
        Board board = Board.create(requestDto.getTitle(), owner, owner.getId());
        Board savedBoard = boardRepository.save(board);

        return BoardResponseDto.builder()
                .boardId(savedBoard.getBoardId())
                .title(savedBoard.getTitle())
                .ownerLoginId(savedBoard.getOwner().getLoginID())
                .build();
    }

    /**
     * 보드 목록 조회 (소유자 또는 공유된 보드)
     */
    public List<BoardResponseDto> getBoards(Long userId) {
        List<Board> boards = boardRepository.findAccessibleBoardsByUserId(userId);

        return boards.stream()
                .map(board -> BoardResponseDto.builder()
                        .boardId(board.getBoardId())
                        .title(board.getTitle())
                        .ownerLoginId(board.getOwner().getLoginID())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 보드 단건 조회 (권한 확인 포함)
     */
    public BoardResponseDto getBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .ownerLoginId(board.getOwner().getLoginID())
                .build();
    }

    /**
     * 보드 제목 수정
     */
    @Transactional
    public BoardResponseDto updateBoardTitle(Long boardId, Long userId, String newTitle) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자이거나 공유 사용자여야 함
        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isSharedUser = board.getSharedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
        
        if (!isOwner && !isSharedUser) {
            throw new IllegalArgumentException("보드를 수정할 권한이 없습니다.");
        }

        board.setTitle(newTitle);
        Board updatedBoard = boardRepository.save(board);

        return BoardResponseDto.builder()
                .boardId(updatedBoard.getBoardId())
                .title(updatedBoard.getTitle())
                .ownerLoginId(updatedBoard.getOwner().getLoginID())
                .build();
    }

    /**
     * 보드 삭제 (소유자만 가능)
     */
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findByBoardIdAndOwnerId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없거나 삭제 권한이 없습니다."));

        boardRepository.delete(board);
    }

    /**
     * 보드에 공유 사용자 추가 (초대 수락 시 사용)
     * @param sharedUser Users 객체 (Controller나 상위 레이어에서 조회하여 전달)
     */
    @Transactional
    public void addSharedUser(Long boardId, Long ownerId, Users sharedUser) {
        Board board = boardRepository.findByBoardIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없거나 권한이 없습니다."));

        board.getSharedUsers().add(sharedUser);
        boardRepository.save(board);
    }
}

