package com.todoapp.shared_todo.domain.board.entity;

import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Board Entity 단위 테스트
 * 비즈니스 로직 검증 (create, equals, hashCode 등)
 */
@DisplayName("Board Entity 테스트")
class BoardTest {

    private User author;
    private String title;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .loginId("testuser")
                .password("password")
                .nickname("Test User")
                .userCode("USER001")
                .provider(ProviderType.LOCAL)
                .status(UsersStatus.CREATED)
                .build();
        ReflectionTestUtils.setField(author, "id", 1L);

        title = "Test Board";
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 Board 생성 성공")
    void create_success() {
        Board board = Board.create(title, author);

        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getAuthor()).isEqualTo(author);
        assertThat(board.getCompletionRate()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("equals와 hashCode 테스트")
    void equalsAndHashCode_test() {
        Board board1 = Board.create(title, author);
        ReflectionTestUtils.setField(board1, "id", 1L);

        Board board2 = Board.create(title, author);
        ReflectionTestUtils.setField(board2, "id", 1L);

        Board board3 = Board.create("Other Board", author);
        ReflectionTestUtils.setField(board3, "id", 2L);

        assertThat(board1).isEqualTo(board2);
        assertThat(board1).isNotEqualTo(board3);
        assertThat(board1.hashCode()).isEqualTo(board2.hashCode());
    }
}

