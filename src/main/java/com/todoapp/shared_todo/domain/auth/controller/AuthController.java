package com.todoapp.shared_todo.domain.auth.controller;

import com.todoapp.shared_todo.domain.auth.dto.request.CheckLoginidRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.SignupRequest;
import com.todoapp.shared_todo.domain.auth.dto.TokenDto;
import com.todoapp.shared_todo.domain.auth.dto.response.AccessTokenResponse;
import com.todoapp.shared_todo.domain.auth.dto.response.AvailableResponse;
import com.todoapp.shared_todo.domain.auth.service.AuthService;
import com.todoapp.shared_todo.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Auth API", description = "인증/인가 관련 API (로컬/소셜 공통)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenValidityInSeconds;
    // ==========================================
    // [Track 1] 로컬 로그인 전용 엔드포인트
    // ==========================================

    @Operation(summary = "회원가입 (로컬)", description = "일반 아이디/비밀번호로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ApiResponse.onSuccess("회원가입 성공");
    }

    @Operation(summary = "로그인 (로컬)", description = "일반 로그인 성공 시 Access Token(Body)과 Refresh Token(Cookie)을 발급합니다.")
    @PostMapping("/login")
    public ApiResponse<AccessTokenResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {

        // 1. 서비스 로직 (ID/PW 검증 및 토큰 생성)
        TokenDto tokenDto = authService.login(request);

        // 2. Refresh Token 쿠키 설정 (7일)
        setRefreshTokenCookie(response, tokenDto.refreshToken(), refreshTokenValidityInSeconds);

        return ApiResponse.onSuccess(new AccessTokenResponse(tokenDto.accessToken()));
    }

    @Operation(summary = "아이디 중복 체크", description = "회원가입 전 아이디 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-id")
    public ResponseEntity<AvailableResponse> checkLoginId(@ModelAttribute @Valid CheckLoginidRequest request) {
        AvailableResponse response = authService.checkLoginIdDuplicate(request);
        return ResponseEntity.ok().body(response);
    }

    // ==========================================
    // [Track 2] 로컬/소셜 공통 엔드포인트
    // ==========================================

    @Operation(summary = "로그아웃", description = "Refresh Token 쿠키를 삭제하고, Access Token을 블랙리스트에 등록합니다.")
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        // 1. 토큰이 없는 경우 (이미 로그아웃됨)
        if (bearerToken == null || refreshToken == null) {
            return ApiResponse.onSuccess("로그아웃 성공 (토큰 없음)");
        }

        // 2. Bearer 제거 및 로그아웃 처리
        String accessToken = resolveToken(bearerToken);
        authService.logout(accessToken, refreshToken);

        // 3. 쿠키 삭제 (MaxAge = 0)
        setRefreshTokenCookie(response, "", 0);

        return ApiResponse.onSuccess("로그아웃 성공");
    }

    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 Refresh Token(Cookie)을 이용해 재발급받습니다.")
    @PostMapping("/reissue")
    public ApiResponse<AccessTokenResponse> reissue(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            // GlobalExceptionHandler에서 처리하거나 명시적 에러 반환
            throw new IllegalArgumentException("Refresh Token 쿠키가 없습니다.");
        }

        // 1. 토큰 재발급 (RTR 방식: Refresh Token도 갱신됨)
        TokenDto tokenDto = authService.reissue(refreshToken);

        // 2. 갱신된 Refresh Token 쿠키 다시 굽기
        setRefreshTokenCookie(response, tokenDto.refreshToken(), refreshTokenValidityInSeconds);

        return ApiResponse.onSuccess(new AccessTokenResponse(tokenDto.accessToken()));
    }

    // ==========================================
    // [Private Methods] 유틸리티
    // ==========================================

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