package com.todoapp.shared_todo.controller;


import com.todoapp.shared_todo.dto.users.LoginRequestDto;
import com.todoapp.shared_todo.dto.users.SignupRequestDto;
import com.todoapp.shared_todo.dto.users.UserResponseDto;
import com.todoapp.shared_todo.entity.Users;
import com.todoapp.shared_todo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //사용자 회원가입 API
    //post /auth/signup
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto dto) {

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
    public ResponseEntity<Map<String,Object>> login(@Valid @RequestBody LoginRequestDto dto) {

    }

    //로그아웃, 리프래시 토큰 폐기

    
}