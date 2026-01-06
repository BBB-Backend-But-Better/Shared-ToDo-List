package com.todoapp.shared_todo.domain.auth.handler;

import com.todoapp.shared_todo.domain.auth.entity.RefreshToken;
import com.todoapp.shared_todo.domain.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todoapp.shared_todo.domain.auth.repository.RefreshTokenRepository;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import com.todoapp.shared_todo.global.security.JwtProvider;
import com.todoapp.shared_todo.global.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        // 1. 인증 정보에서 유저 추출
        CustomePrincipal oAuth2User = (CustomePrincipal) authentication.getPrincipal();

        Long userId = oAuth2User.getUserId(); // CustomOAuth2User에 getter 필요
        String loginId = oAuth2User.getLoginId();
        String nickname = oAuth2User.getName(); // or getNickname()
        String userCode = oAuth2User.getUsercode();
        ProviderType provider = oAuth2User.getProvider();


        // 2. 토큰 생성
        String accessToken = jwtProvider.createAccessToken(userId,loginId,nickname,provider, userCode);
        String refreshToken = jwtProvider.createRefreshToken(userId,loginId,userCode);

        // 3. Redis에 Refresh Token 저장 (Key: "RT:loginId", Value: refreshToken)
        RefreshToken RefreshTokenEntity = RefreshToken.builder()
                .userLoginId(oAuth2User.getLoginId())
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(RefreshTokenEntity);

        // 4. 쿠키 설정 (Access Token & Refresh Token)
        CookieUtils.addCookie(response, accessToken, refreshToken,7);

        // 5. 인증 과정에서 생긴 임시 쿠키(JSESSIONID 대체제) 삭제
        clearAuthenticationAttributes(request, response);

        // 6. 리다이렉트 (프론트엔드로)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 5. 인증 과정에서 생긴 임시 쿠키(JSESSIONID 대체제) 삭제
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
