package com.todoapp.shared_todo.domain.invitation.entity;

import com.todoapp.shared_todo.domain.board.entity.Board;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "invitation",
        indexes = {
                @Index(name = "idx_invitation_invitee", columnList = "invitee_id"),
                @Index(name = "idx_invitation_board", columnList = "board_id")
        }
)
public class Invitation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 초대할 보드 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /* 초대한 사용자 (CRUD 권한 보유자) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    /* 초대받은 사용자 (아직 BoardMember 아님) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    /* 초대 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    /* 초대 만료 시각 */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    private Invitation(
            Board board,
            User inviter,
            User invitee,
            LocalDateTime expiresAt
    ) {
        this.board = board;
        this.inviter = inviter;
        this.invitee = invitee;
        this.expiresAt = expiresAt;
    }

    /* ===== Factory ===== */

    public static Invitation create(
            Board board,
            User inviter,
            User invitee,
            LocalDateTime expiresAt
    ) {
        return new Invitation(board, inviter, invitee, expiresAt);
    }

    /* ===== Domain Logic ===== */

    public void accept() {
        validatePending();
        validateNotExpired();
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() {
        validatePending();
        this.status = InvitationStatus.REJECTED;
    }

    private void validatePending() {
        if (this.status != InvitationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 초대입니다.");
        }
    }

    private void validateNotExpired() {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("초대가 만료되었습니다.");
        }
    }
}