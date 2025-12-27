package com.todoapp.shared_todo.domain.board.entity;

import com.todoapp.shared_todo.domain.task.entity.Task;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    // 정적 팩토리 메서드
    public static Board create(String title, User author) {
        Board board = new Board();
        board.setTitle(title);
        board.setAuthor(author);
        board.setCompletionRate(0.0f);
        return board;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    // 보드 소유자 (최초 생성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "completion_rate")
    private Float completionRate = 0.0f;

    // 이 보드에 속한 Task 리스트들
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
