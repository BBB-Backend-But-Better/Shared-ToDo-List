/*
package com.todoapp.shared_todo.domain.auth.entity;


import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OldRefreshToken extends BaseTimeEntity {

    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //단순 String 컬럼으로 저장
    @Column(name = "user_login_id",nullable = false, length = 50)
    private String userLoginId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private boolean revoked = false; // 기본값 false

    @Builder
    public RefreshToken(String userLoginId, String token, Boolean revoked) {

        Assert.hasText(userLoginId, "user_login_Id는 필수입니다.");
        Assert.hasText(token, "token은 필수입니다.");

        this.userLoginId = userLoginId;
        this.token = token;
        this.revoked = (revoked != null) ? revoked : false; //리커브가 null이면 flase
    }

    // 토큰 만료 처리 (로그아웃 등)
    public void revoke() {
        this.revoked = true;
    }
}
*/
