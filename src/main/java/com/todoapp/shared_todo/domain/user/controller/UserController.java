package com.todoapp.shared_todo.domain.user.controller;

import com.todoapp.shared_todo.domain.user.dto.request.ChangeNicknameRequest;
import com.todoapp.shared_todo.domain.user.dto.request.ChangePasswordRequest;
import com.todoapp.shared_todo.domain.user.dto.request.PasswordCheckRequest;
import com.todoapp.shared_todo.domain.user.dto.response.UserResponse;
import com.todoapp.shared_todo.domain.user.service.UserService;
import com.todoapp.shared_todo.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "마이페이지/내 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo")
public class UserController {

    private final UserService userService;

//내정보 조회
@Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 프로필 정보(닉네임 등)를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails loginId){
        UserResponse userResponse = userService.getMyProfile(loginId.getUsername());
        return ResponseEntity.ok().body(userResponse);
    }
//닉네임 변경
@Operation(summary = "닉네임 변경", description = "로그인한 유저의 닉네임을 변경합니다.")
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserResponse> changeNickname(
            @AuthenticationPrincipal CustomUserDetails loginId,
            @Valid@RequestBody ChangeNicknameRequest request){

        UserResponse userResponse = userService.changeNickname(loginId.getUsername(),request);
        return ResponseEntity.ok().body(userResponse);
    }
//비밀번호 변경
@Operation(summary = "비밀번호 변경", description = "로그인한 유저의 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails loginId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(loginId.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    //비밀번호 확인
    @Operation(summary = "비밀번호 확인", description = "중요 정보 수정 전, 현재 비밀번호가 맞는지 확인합니다.")
    @PostMapping("/me/passwrod-check")
    public ResponseEntity<Void> checkPassword(
            @AuthenticationPrincipal CustomUserDetails loginId,
            @Valid @RequestBody PasswordCheckRequest request
    ){
        userService.checkPassword(loginId.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    //회원탈퇴
    @Operation(summary = "회원 탈퇴", description = "서비스에서 탈퇴하고 계정을 삭제합니다. (복구 불가)")
    @PostMapping("/me/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal CustomUserDetails userId){
        userService.withdraw(userId.getUsername());
        return ResponseEntity.noContent().build();
    }

}
