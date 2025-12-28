package com.todoapp.shared_todo.domain.boardMember.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "board_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"})
)
public class BoardMember {

    public static BoardMember create(Board board, User user, BoardMemberRole role) {
        BoardMember member = new BoardMember();
        member.board = board;
        member.user = user;
        member.role = role;
        return member;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardMemberRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public void changeRole(BoardMemberRole role) {
        this.role = role;
    }
}