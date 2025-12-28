package com.todoapp.shared_todo.domain.auth.controller;

import com.todoapp.shared_todo.domain.auth.dto.request.CheckLoginidRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.SignupRequest;
import com.todoapp.shared_todo.domain.auth.dto.TokenDto;
import com.todoapp.shared_todo.domain.auth.dto.response.AccessTokenResponse;
import com.todoapp.shared_todo.domain.auth.dto.response.AvailableResponse;
import com.todoapp.shared_todo.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup (@RequestBody @Valid SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //로그인(httpolny 쿠키 발급)
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
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refresh_token") String refreshToken) {
        authService.logout(refreshToken);

        ResponseCookie endCookie = ResponseCookie.from("refresh_token", "")
                .path("/") //모든 경로 접근 가능
                .sameSite("Strict") //csrf 공격 방지
                .httpOnly(true) //자바 스크립트 접근 차단
                .secure(false) //https에서만 전송, 로컬일때는 false
                .maxAge(7 * 24 * 60 * 60)
                .build();

        //액세서 토큰 바디에 답아서 보내기
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, endCookie.toString())
                .body("로그아웃 성공");
    }

    //토큰 재발급
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
    @GetMapping("/check-id")
    public ResponseEntity<AvailableResponse> checkLoginid(@Valid CheckLoginidRequest request) {
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