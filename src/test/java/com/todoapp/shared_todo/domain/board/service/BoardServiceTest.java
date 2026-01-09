package com.todoapp.shared_todo.domain.board.service;

import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequest;
import com.todoapp.shared_todo.domain.board.dto.BoardResponse;
import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.board.repository.BoardRepository;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Board Service 단위 테스트
 * 외부 의존성(Repository)을 Mock으로 처리
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Board Service 테스트")
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private BoardService boardService;

    private User user;
    private Board board;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .loginId("testuser")
                .password("password")
                .nickname("Test User")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        board = Board.create("Test Board", user);
        board.setId(1L);
    }

    @Test
    @DisplayName("보드 생성 성공")
    void createBoard_success() {
        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("New Board")
                .build();

        given(usersRepository.findById(1L)).willReturn(Optional.of(user));
        given(boardRepository.save(any(Board.class))).willAnswer(invocation -> {
            Board saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        BoardResponse response = boardService.createBoard(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New Board");
        assertThat(response.getAuthorId()).isEqualTo(1L);
        assertThat(response.getCompletionRate()).isEqualTo(0.0f);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("보드 생성 실패 - 사용자 없음")
    void createBoard_failure_userNotFound() {
        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("New Board")
                .build();

        given(usersRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createBoard(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("보드 목록 조회 성공")
    void getBoards_success() {
        Board board2 = Board.create("Board2", user);
        board2.setId(2L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(user));
        given(boardRepository.findByAuthor(user)).willReturn(List.of(board, board2));

        List<BoardResponse> responses = boardService.getBoards(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Test Board");
        assertThat(responses.get(1).getTitle()).isEqualTo("Board2");
    }

    @Test
    @DisplayName("보드 목록 조회 실패 - 사용자 없음")
    void getBoards_failure_userNotFound() {
        given(usersRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoards(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("보드 단건 조회 성공")
    void getBoard_success() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        BoardResponse response = boardService.getBoard(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Board");
    }

    @Test
    @DisplayName("보드 단건 조회 실패 - 보드 없음")
    void getBoard_failure_boardNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoard(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("보드 단건 조회 실패 - 권한 없음")
    void getBoard_failure_noPermission() {
        User otherUser = User.builder()
                .loginId("otheruser")
                .password("password")
                .nickname("Other User")
                .userCode("USER002")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.getBoard(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드에 접근할 권한이 없습니다");
    }

    @Test
    @DisplayName("보드 제목 수정 성공")
    void updateBoardTitle_success() {
        String newTitle = "Updated Title";

        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(boardRepository.save(any(Board.class))).willAnswer(invocation -> {
            Board saved = invocation.getArgument(0);
            saved.setTitle(newTitle);
            return saved;
        });

        BoardResponse response = boardService.updateBoardTitle(1L, 1L, newTitle);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(newTitle);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("보드 제목 수정 실패 - 보드 없음")
    void updateBoardTitle_failure_boardNotFound() {
        given(boardRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.updateBoardTitle(1L, 1L, "Updated Title"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("보드 제목 수정 실패 - 권한 없음")
    void updateBoardTitle_failure_noPermission() {
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.updateBoardTitle(1L, 2L, "Updated Title"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드를 수정할 권한이 없습니다");
    }

    @Test
    @DisplayName("보드 삭제 성공")
    void deleteBoard_success() {
        given(usersRepository.findById(1L)).willReturn(Optional.of(user));
        given(boardRepository.findByIdAndAuthor(1L, user)).willReturn(Optional.of(board));

        boardService.deleteBoard(1L, 1L);

        verify(boardRepository).delete(board);
    }

    @Test
    @DisplayName("보드 삭제 실패 - 권한 없음")
    void deleteBoard_failure_noPermission() {
        given(usersRepository.findById(1L)).willReturn(Optional.of(user));
        given(boardRepository.findByIdAndAuthor(1L, user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.deleteBoard(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("보드를 찾을 수 없거나 삭제 권한이 없습니다");
    }
}

