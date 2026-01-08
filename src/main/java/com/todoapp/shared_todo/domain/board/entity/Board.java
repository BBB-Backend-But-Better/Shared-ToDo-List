package com.todoapp.shared_todo.domain.board.entity;

import com.todoapp.shared_todo.domain.task.entity.Task;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

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

    @Builder
    public Board(String title, User author, Float completionRate) {
        this.title = title;
        this.author = author;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //타이틀 업데이트
    public void updateTitle(String title) {
        Assert.hasText(title, "닉네임은 필수입니다.");
        this.title = title;
    }
}
