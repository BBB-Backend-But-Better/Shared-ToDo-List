package com.todoapp.shared_todo.domain.board.service;

import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequest;
import com.todoapp.shared_todo.domain.board.dto.BoardResponse;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMember;
import com.todoapp.shared_todo.domain.boardMember.entity.BoardMemberRole;
import com.todoapp.shared_todo.domain.boardMember.repository.BoardMemberRepository;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
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
    private final UsersRepository usersRepository;
    private final BoardMemberRepository boardMemberRepository;

    /**
     * 보드 생성
     * 요구사항: 보드 생성 시 소유자 자동 설정(만든 유저)
     */
    @Transactional
    public BoardResponse createBoard(Long userId, BoardCreateRequest request) {
        
        //먼저 사용자 찾기
        User author = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(request.getTitle())
                .author(author)
                .build();

        //먼저 보드 만들고
        Board savedBoard = boardRepository.save(board);

        //보드 맴버 만들고 보드 맴버에도 저장
        BoardMember ownerMember = BoardMember.create(board, author, BoardMemberRole.OWNER);
        boardMemberRepository.save(ownerMember);


        return BoardResponse.builder()
                .id(savedBoard.getId())
                .title(savedBoard.getTitle())
                .authorId(savedBoard.getAuthor().getId())
                .completionRate(savedBoard.getCompletionRate())
                .build();
    }

    /**
     * 보드 목록 조회
     * 요구사항: 보드 목록 조회 시 소유한 보드만 조회
     */
    public List<BoardResponse> getBoards(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 소유한 보드 목록
        List<BoardMember> myMemberships = boardMemberRepository.findAllByUserId(userId);

        return myMemberships.stream()
                .map(bm -> {
                    Board board = bm.getBoard(); // 조인해와서 바로 꺼낼 수 있음
                    return BoardResponse.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .authorId(board.getAuthor().getId())
                            .completionRate(board.getCompletionRate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 보드 단건 조회
     */
    public BoardResponse getBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        boolean exists = boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
        // 권한 확인: 소유자만 접근 가능
        if (!exists) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .authorId(board.getAuthor().getId())
                .completionRate(board.getCompletionRate())
                .build();
    }

    /**
     * 보드 제목 수정
     * 요구사항: 보드 제목 수정 시 권한 확인(소유자)
     */
    @Transactional
    public BoardResponse updateBoardTitle(Long boardId, Long userId, String newTitle) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없습니다."));

        // 권한 확인: 소유자만 수정 가능
        boolean exists = boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
        // 권한 확인: 소유자만 접근 가능
        if (!exists) {
            throw new IllegalArgumentException("보드에 접근할 권한이 없습니다.");
        }

        board.updateTitle(newTitle);
        Board updatedBoard = boardRepository.save(board);

        return BoardResponse.builder()
                .id(updatedBoard.getId())
                .title(updatedBoard.getTitle())
                .authorId(updatedBoard.getAuthor().getId())
                .completionRate(updatedBoard.getCompletionRate())
                .build();
    }

    /**
     * 보드 삭제
     * 요구사항: 보드 삭제 시 소유자만 가능
     */
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findByIdAndAuthor(boardId, 
                usersRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")))
                .orElseThrow(() -> new IllegalArgumentException("보드를 찾을 수 없거나 삭제 권한이 없습니다."));

        boardRepository.delete(board);
    }

}
