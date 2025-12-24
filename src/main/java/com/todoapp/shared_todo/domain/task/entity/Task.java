package com.todoapp.shared_todo.domain.task.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseTimeEntity {

    // 정적 팩토리 메서드
    public static Task create(String content, Board board) {
        Task task = new Task();
        task.setContent(content);
        task.setBoard(board);
        return task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long taskId;

    @Column(length = 100, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.UNCHECKED;

    // 소속 보드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 상태 토글 메서드
    public void toggleStatus() {
        this.status = (this.status == TaskStatus.UNCHECKED)
                ? TaskStatus.CHECKED
                : TaskStatus.UNCHECKED;
    }
}

