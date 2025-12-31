package com.todoapp.shared_todo.domain.task.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseTimeEntity {

    // 정적 팩토리 메서드
    public static Task create(String description, Board board, LocalDateTime dueDate) {
        Task task = new Task();
        task.setDescription(description);
        task.setBoard(board);
        task.setStatus(TaskStatus.UNCHECKED);
        task.setDueDate(dueDate);
        // 기존 코드: task.setCompleted(false);
        return task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // 기존 코드: @Column(nullable = false)
    // 기존 코드: private Boolean completed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.UNCHECKED;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // 소속 보드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 완료 상태 토글 메서드
    public void toggleStatus() {
        if (this.status == TaskStatus.UNCHECKED) {
            this.status = TaskStatus.CHECKED;
        } else {
            this.status = TaskStatus.UNCHECKED;
        }
    }

    // 기존 코드: public void toggleCompleted() {
    // 기존 코드:     this.completed = !this.completed;
    // 기존 코드: }
}

