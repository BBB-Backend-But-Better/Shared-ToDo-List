package com.todoapp.shared_todo.domain.auth.entity;


import com.todoapp.shared_todo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

//초 * 분 * 시 * 일
@Getter
@RedisHash(value = "refresh_token_loginId")
public class RefreshToken{

    @Id
    private String userLoginId; //key
    private String token;       //value

    @Builder
    public RefreshToken(String userLoginId, String token) {

        Assert.hasText(userLoginId, "user_login_Id는 필수입니다.");
        Assert.hasText(token, "token은 필수입니다.");

        this.userLoginId = userLoginId;
        this.token = token;
    }

    @TimeToLive

    private Long expiration;
}

