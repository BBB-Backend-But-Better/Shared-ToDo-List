package com.todoapp.shared_todo.domain.boardMember.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"board_id", "user_id"})
})
public class BoardMember {

    // 정적 팩토리 메서드
    public static BoardMember create(Board board, User user, String role) {
        BoardMember boardMember = new BoardMember();
        boardMember.setBoard(board);
        boardMember.setUser(user);
        boardMember.setRole(role);
        return boardMember;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속 보드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 보드 멤버 (유저)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String role = "OWNER";

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    @SuppressWarnings("unused") // JPA가 런타임에 자동으로 호출하는 메서드
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}