package com.todoapp.shared_todo.domain.auth.handler;
import com.todoapp.shared_todo.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.todoapp.shared_todo.domain.auth.entity.RefreshToken;
import com.todoapp.shared_todo.domain.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todoapp.shared_todo.domain.auth.repository.RefreshTokenRepository;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import com.todoapp.shared_todo.global.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomePrincipal customPrincipal; // Principal Mock

    @Test
    @DisplayName("네이버 로그인 성공 시 토큰 생성, Redis 저장, 쿠키 설정, 리다이렉트가 수행되어야 한다")
    void onAuthenticationSuccess_Test() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 1. 인증 정보 Mocking (네이버에서 넘어왔다고 가정)
        given(authentication.getPrincipal()).willReturn(customPrincipal);
        given(customPrincipal.getUserId()).willReturn(1L);
        given(customPrincipal.getLoginId()).willReturn("naver_12345");
        given(customPrincipal.getName()).willReturn("테스터");
        given(customPrincipal.getUsercode()).willReturn("USER_CODE_123");
        given(customPrincipal.getProvider()).willReturn(ProviderType.NAVER); // ProviderType 가정

        // 2. JWT Provider Mocking (가짜 토큰 반환)
        String fakeAccessToken = "fake_access_token";
        String fakeRefreshToken = "fake_refresh_token";

        given(jwtProvider.createAccessToken(any(), any(), any(), any(), any()))
                .willReturn(fakeAccessToken); // [cite: 9]
        given(jwtProvider.createRefreshToken(any(), any(), any()))
                .willReturn(fakeRefreshToken); // [cite: 9]

        // when
        successHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        // 1. Redis 저장 검증
        verify(refreshTokenRepository).save(any(RefreshToken.class)); // [cite: 10]

        // 2. 쿠키 설정 검증 (CookieUtils 구현에 따라 쿠키 이름은 다를 수 있음, 여기선 access_token으로 가정)
        // 주의: CookieUtils 구현 방식에 따라 response.getCookies()로 확인 가능한지 체크 필요
        Cookie[] cookies = response.getCookies();
        assertThat(cookies).isNotEmpty();
        // CookieUtils 로직이 response.addCookie를 했다면 여기서 확인 가능

        // 3. 리다이렉트 URL 검증
        String redirectedUrl = response.getRedirectedUrl();
        assertThat(redirectedUrl).isNotNull();
        System.out.println("Redirected URL: " + redirectedUrl);
        // 로그로 실제 리다이렉트 URL 확인 (쿼리 파라미터 등)
    }
}