package com.todoapp.shared_todo.domain.user.entity;

import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.util.Assert;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true) //UK
    private String loginId; //로컬로그인 아이디, 소셜 로그인 할떄에는 provider + _ + providerId (예: google_1029384812)으로 저장할꺼임.

    @Column(length = 100)
    private String password; // 'bcrypt 암호화된 비밀번호', 소셜 유저는 비번이 필요없음. null넣어버리자.

    @Column(length = 30, nullable = false)
    private String nickname;

    //랜덤값을 서비스단에서 생성해서 넣어주기
    @Column(name = "user_code", length = 30, nullable = false, unique = true)
    private String userCode;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Column(length = 255) //SNS 로그인 식별자
    private String providerId;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private UsersStatus status;

    @Builder
    private User(String loginId, String password, String nickname, String userCode, ProviderType provider, String providerId, UsersStatus status) {

        Assert.hasText(loginId, "loginId는 필수입니다.");
        Assert.hasText(nickname, "nickname은 필수입니다.");
        Assert.hasText(userCode, "userCode는 필수입니다.");

        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.userCode = userCode;
        this.providerId = providerId;

        this.provider = (provider != null) ? provider : ProviderType.LOCAL;
        this.status = (status != null)? status : UsersStatus.CREATED;


    }

    //비지니스 메서드

    public void updateNickname(String newNickname) {
        Assert.hasText(newNickname, "닉네임은 필수입니다.");
        this.nickname = newNickname;
    }

    public void updatePassword(String encryptedPassword) {
        Assert.hasText(encryptedPassword, "비밀번호는 필수입니다.");
        this.password = encryptedPassword;
    }

    // 회원 탈퇴 (Soft Delete)
    public void withdraw() {
        this.status = UsersStatus.DELETED;
    }

}

