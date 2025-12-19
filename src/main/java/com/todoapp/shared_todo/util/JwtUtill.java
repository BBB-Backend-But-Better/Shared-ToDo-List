package com.todoapp.shared_todo.util;

import org.springframework.stereotype.Component;

@Component
public class JwtUtill {

    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15; // 15분
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
}
