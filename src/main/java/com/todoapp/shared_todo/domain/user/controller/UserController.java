package com.todoapp.shared_todo.domain.user.controller;

import com.todoapp.shared_todo.domain.user.dto.request.ChangeNicknameRequest;
import com.todoapp.shared_todo.domain.user.dto.request.ChangePasswordRequest;
import com.todoapp.shared_todo.domain.user.dto.request.PasswordCheckRequest;
import com.todoapp.shared_todo.domain.user.dto.response.UserResponse;
import com.todoapp.shared_todo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo")
public class UserController {

    private final UserService userService;

//내정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal long userid){
        UserResponse userResponse = userService.getMyProfile(userid);
        return ResponseEntity.ok().body(userResponse);
    }
//닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserResponse> changeNickname(
            @AuthenticationPrincipal long userid,
            @Valid@RequestBody ChangeNicknameRequest request){

        UserResponse userResponse = userService.changeNickname(userid,request);
        return ResponseEntity.ok().body(userResponse);
    }
//비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    //비밀번호 확인
    @PostMapping("/me/passwrod-check")
    public ResponseEntity<Void> checkPassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordCheckRequest request
    ){
        userService.checkPassword(userId, request);
        return ResponseEntity.ok().build();
    }

    //회원탈퇴
    @PostMapping("/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal Long userId){
        userService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }

}
