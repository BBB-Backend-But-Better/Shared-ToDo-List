package com.todoapp.shared_todo.domain.auth.controller;

import com.todoapp.shared_todo.domain.auth.dto.request.CheckLoginidRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.SignupRequest;
import com.todoapp.shared_todo.domain.auth.dto.TokenDto;
import com.todoapp.shared_todo.domain.auth.dto.response.AccessTokenResponse;
import com.todoapp.shared_todo.domain.auth.dto.response.AvailableResponse;
import com.todoapp.shared_todo.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증/인가 관련 API (회원가입, 로그인, 토큰 재발급)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //회원가입
    @Operation(summary = "회원가입", description = "신규 유저 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup (@RequestBody @Valid SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //로그인(httpolny 쿠키 발급)
    @Operation(summary = "로그인", description = "로그인 성공 시 Access Token을 Body로, Refresh Token을 HttpOnly Cookie로 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login (@RequestBody @Valid LoginRequest request) {

        //서비스로 부터 토큰 2개 받아오기
        TokenDto tokenDto = authService.login(request);

        ResponseCookie refreshCookie = createRefreshCookie(tokenDto.refreshToken());

        //액세서 토큰 바디에 답아서 보내기
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(tokenDto.accessToken()));
    }


    //로그아웃
    @Operation(summary = "로그아웃", description = "Refresh Token 쿠키를 삭제하고 서버(Redis 등)에서 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refresh_token") String refreshToken) {
        authService.logout(refreshToken);

        ResponseCookie endCookie = ResponseCookie.from("refresh_token", "")
                .path("/") //모든 경로 접근 가능
                .sameSite("Strict") //csrf 공격 방지
                .httpOnly(true) //자바 스크립트 접근 차단
                .secure(false) //https에서만 전송, 로컬일때는 false
                .maxAge(0)
                .build();

        //액세서 토큰 바디에 답아서 보내기
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, endCookie.toString())
                .body("로그아웃 성공");
    }

    //토큰 재발급
    @Operation(summary = "토큰 재발급", description = "Refresh Token 쿠키를 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(@CookieValue("refresh_token") String refreshToken) {

        TokenDto tokenDto = authService.reissue(refreshToken);

        ResponseCookie refreshCookie = createRefreshCookie(tokenDto.refreshToken());

        //액세서 토큰 바디에 답아서 보내기
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(tokenDto.accessToken()));

    }

    //아이디 중복 체크
    @Operation(summary = "아이디 중복 체크", description = "회원가입 전 로그인 아이디 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-id")
    public ResponseEntity<AvailableResponse> checkLoginid(@ModelAttribute @Valid CheckLoginidRequest request) {
        AvailableResponse response = authService.checkLoginIdDuplicate(request);
        return  ResponseEntity.ok()
                .body(response);
    }


    private static ResponseCookie createRefreshCookie(String tokenDto) {
        //리플래시 토큰 httponly쿠키로
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenDto)
                .path("/") //모든 경로 접근 가능
                .sameSite("Strict") //csrf 공격 방지
                .httpOnly(true) //자바 스크립트 접근 차단
                .secure(false) //https에서만 전송, 로컬일때는 false
                .maxAge(7 * 24 * 60 * 60)
                .build();
        return refreshCookie;
    }
}