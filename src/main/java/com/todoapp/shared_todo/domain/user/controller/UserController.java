package com.todoapp.shared_todo.domain.user.controller;

import com.todoapp.shared_todo.domain.user.dto.request.ChangeNicknameRequest;
import com.todoapp.shared_todo.domain.user.dto.request.ChangePasswordRequest;
import com.todoapp.shared_todo.domain.user.dto.request.PasswordCheckRequest;
import com.todoapp.shared_todo.domain.user.dto.response.UserResponse;
import com.todoapp.shared_todo.domain.user.service.UserService;
import com.todoapp.shared_todo.global.dto.ApiResponse;
import com.todoapp.shared_todo.global.exception.ErrorCode;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<UserResponse> getMyProfile(@AuthenticationPrincipal CustomePrincipal loginId){
        UserResponse userResponse = userService.getMyProfile(loginId.getLoginId());
        return ApiResponse.onSuccess(userResponse);
    }
//닉네임 변경
@Operation(summary = "닉네임 변경", description = "로그인한 유저의 닉네임을 변경합니다.")
    @PatchMapping("/me/nickname")
    public ApiResponse<UserResponse> changeNickname(
            @AuthenticationPrincipal CustomePrincipal loginId,
            @Valid@RequestBody ChangeNicknameRequest request){

        UserResponse userResponse = userService.changeNickname(loginId.getUsername(),request);
    return ApiResponse.onSuccess(userResponse);
    }
//비밀번호 변경
@Operation(summary = "비밀번호 변경", description = "로그인한 유저의 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal CustomePrincipal loginId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(loginId.getUsername(), request);
        return ApiResponse.onSuccess(null);
    }

    //비밀번호 확인
    @Operation(summary = "비밀번호 확인", description = "중요 정보 수정 전, 현재 비밀번호가 맞는지 확인합니다.")
    @PostMapping("/me/passwrod-check")
    public ApiResponse<Void> checkPassword(
            @AuthenticationPrincipal CustomePrincipal loginId,
            @Valid @RequestBody PasswordCheckRequest request
    ){
        userService.checkPassword(loginId.getUsername(), request);
        return ApiResponse.onSuccess(null);
    }

    //회원탈퇴
    @Operation(summary = "회원 탈퇴", description = "서비스에서 탈퇴하고 계정을 삭제합니다. (복구 불가)")
    @PostMapping("/me/withdraw")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal CustomePrincipal userId){
        userService.withdraw(userId.getUsername());
        return ApiResponse.onSuccess(null);
    }

}
