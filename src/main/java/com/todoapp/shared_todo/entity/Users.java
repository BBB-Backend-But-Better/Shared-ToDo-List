package com.todoapp.shared_todo.entity;

import com.todoapp.shared_todo.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "user_id")
    private Long userId;

    @Column(length = 15, nullable = false, unique = true)
    private String loginID; //로그인 아이디

    @Column(length = 100, nullable = false, unique = true)
    private String password; // 'bcrypt 암호화된 비밀번호'

}
