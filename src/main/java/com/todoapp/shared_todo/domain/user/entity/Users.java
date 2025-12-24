package com.todoapp.shared_todo.domain.user.entity;

import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    @NotBlank // null + 빈 문자열("") 모두 막음
    private String loginID; //로그인 아이디

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank
    private String password; // 'bcrypt 암호화된 비밀번호'
}
