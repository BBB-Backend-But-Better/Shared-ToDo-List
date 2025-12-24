package com.todoapp.shared_todo.domain.auth.controller;


import com.todoapp.shared_todo.domain.auth.dto.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.SignupRequest;
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


}