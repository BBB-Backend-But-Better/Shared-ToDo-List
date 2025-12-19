package com.todoapp.shared_todo.entity;

import com.todoapp.shared_todo.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoList extends BaseEntity {

    // 정적 팩토리 메서드
    public static TodoList create(String content, Board board, Long createdBy) {
        TodoList todoList = new TodoList();
        todoList.setContent(content);
        todoList.setBoard(board);
        todoList.setCreatedBy(createdBy);
        return todoList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long todoId;

    @Column(length = 100, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status = TodoStatus.UNCHECKED;

    // 소속 보드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 상태 토글 메서드
    public void toggleStatus() {
        this.status = (this.status == TodoStatus.UNCHECKED) 
                ? TodoStatus.CHECKED 
                : TodoStatus.UNCHECKED;
    }
}