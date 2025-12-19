package com.todoapp.shared_todo.entity;

import com.todoapp.shared_todo.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    // 정적 팩토리 메서드
    public static Board create(String title, Users owner, Long createdBy) {
        Board board = new Board();
        board.setTitle(title);
        board.setOwner(owner);
        board.setCreatedBy(createdBy);
        return board;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(length = 50, nullable = false)
    private String title;

    // 보드 소유자 (users.user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    // 이 보드에 속한 투두 리스트들
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TodoList> todoLists = new ArrayList<>();

    // 공유된 사용자들 (boards_shared_users 조인 테이블)
    @ManyToMany
    @JoinTable(
            name = "boards_shared_users",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<Users> sharedUsers = new HashSet<>();
}