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

    @Column(length = 30, nullable = false, unique = true) //UK
    private String loginId; //로그인 아이디

    @Column(length = 100)
    private String password; // 'bcrypt 암호화된 비밀번호'

    @Column(length = 30, nullable = false)
    private String nickname;

    //랜덤값을 서비스단에서 생성해서 넣어주기
    @Column(length = 30, nullable = false, unique = true)
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
}

/**
 *  User(사용자)[user는 예약어이기에 users로 수정]
 *
 * - **역할:** 앱의 모든 사용자 관리.
 * - **필드:**
 *     - `id` (bigserial, **PK**): 내부 식별자.
 *     - `login_id` (varchar(20), **UK**, Not Null): 로그인 ID 및 공유 식별자 (@user123).
 *     - `password` (varchar(255), Nullable): 로컬 로그인용 해시 비번. SNS는 Null.
 *SNS 로그인할때에는 비밀번호가 필요없으니 NULL, API를 달리 PASSWORD 받을수있게 해주기
 *
 *     - `nickname` (varchar(50)): 화면 표시 이름.
 *     - `user_code` (varchar(15), Not null, Unique.UUID) : 유저의 고유 식별 코드(UUID), 초대코드
 *     - `provider` (varchar(50), default 'local'): 로그인 제공자. (Enum: 구글, 카카오, 네이버)
 *     - `provider_id` (varchar(255)): SNS 식별자.
 *     - `status` (varchar(20), default ‘CREATED'): (Enum: CREATED, DELETED)
 * - **관계:**
 *     - 1:N `Board` (`author_id`): 내가 **만든** 보드들.
 *     - 1:N `BoardMember` (`user_id`): 내가 **참여 중인** 모든 보드들 (내가 만든 것 + 초대받은 것).
 *     - 1:N `RefreshTokens` (`user_login_id`).
 * */
