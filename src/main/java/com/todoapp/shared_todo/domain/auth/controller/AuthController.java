package com.todoapp.shared_todo.domain.auth.controller;


import com.todoapp.shared_todo.domain.auth.dto.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.SignupRequest;
import com.todoapp.shared_todo.dto.users.UserResponseDto;
import com.todoapp.shared_todo.domain.user.entity.Users;
import com.todoapp.shared_todo.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //사용자 회원가입 API
    //post /auth/signup
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequest dto) {

        Users userId = authService.signUp(dto);

        UserResponseDto response = UserResponseDto.builder()
                .id(userId.getId())
                .loginId(dto.getLoginId())
                .build();


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * 사용자 로그인
     * jwt 인틍 토큰 작업 해야됨
     * post /auth/login
     */

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@Valid @RequestBody LoginRequest dto) {

    }

    //로그아웃, 리프래시 토큰 폐기

    
}