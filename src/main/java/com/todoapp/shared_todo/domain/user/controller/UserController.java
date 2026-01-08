package com.todoapp.shared_todo.domain.user.controller;

import com.todoapp.shared_todo.domain.auth.service.AuthService;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "마이페이지/내 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo")
public class UserController {

    private final AuthService authService;
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
    public ApiResponse<String> withdraw(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            @AuthenticationPrincipal CustomePrincipal userId,
            HttpServletResponse response){
        // 1. 토큰이 없는 경우 (이미 로그아웃됨)
        if (bearerToken == null || refreshToken == null) {
            return ApiResponse.onSuccess("로그아웃 성공 (토큰 없음)");
        }

        // 2. Bearer 제거 및 로그아웃 처리
        String accessToken = resolveToken(bearerToken);
        authService.logout(accessToken, refreshToken);

        // 3. 쿠키 삭제 (MaxAge = 0)
        setRefreshTokenCookie(response, "", 0);
        userService.withdraw(userId.getLoginId());
        return ApiResponse.onSuccess(null);
    }


    private void setRefreshTokenCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(false) // ★ 배포 시 true로 변경 (SSL 적용 시)
                .maxAge(maxAgeSeconds)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // Bearer가 없으면 그대로 반환해서 검증 실패 유도
    }

}
