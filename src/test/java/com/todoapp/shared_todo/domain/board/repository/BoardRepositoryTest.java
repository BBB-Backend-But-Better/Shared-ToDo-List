package com.todoapp.shared_todo.domain.board.repository;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Board Repository 통합 테스트
 * JPA 쿼리 및 DB 연동 검증
 */
@DataJpaTest
@DisplayName("Board Repository 테스트")
@SuppressWarnings("unused")
class BoardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    private User user1;
    private User user2;
    private Board board1;
    private Board board2;
    private Board board3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .loginId("user1")
                .password("password")
                .nickname("User1")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        user1 = entityManager.persistAndFlush(user1);

        user2 = User.builder()
                .loginId("user2")
                .password("password")
                .nickname("User2")
                .userCode("USER002")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        user2 = entityManager.persistAndFlush(user2);

        board1 = Board.create("Board1", user1);
        board1 = entityManager.persistAndFlush(board1);

        board2 = Board.create("Board2", user1);
        board2 = entityManager.persistAndFlush(board2);

        board3 = Board.create("Board3", user2);
        board3 = entityManager.persistAndFlush(board3);
    }

    @Test
    @DisplayName("보드 ID와 소유자로 조회 성공")
    void findByIdAndAuthor_success() {
        Optional<Board> found = boardRepository.findByIdAndAuthor(board1.getId(), user1);

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(board1);
        assertThat(found.get().getAuthor()).isEqualTo(user1);
    }

    @Test
    @DisplayName("보드 ID와 소유자로 조회 실패 - 다른 소유자")
    void findByIdAndAuthor_failure_differentOwner() {
        Optional<Board> found = boardRepository.findByIdAndAuthor(board1.getId(), user2);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("사용자가 소유한 보드 목록 조회 성공")
    void findByAuthor_success() {
        List<Board> user1Boards = boardRepository.findByAuthor(user1);
        List<Board> user2Boards = boardRepository.findByAuthor(user2);

        assertThat(user1Boards).hasSize(2);
        assertThat(user1Boards).containsExactlyInAnyOrder(board1, board2);
        assertThat(user2Boards).hasSize(1);
        assertThat(user2Boards).contains(board3);
    }
}

