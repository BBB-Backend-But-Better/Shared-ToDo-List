package com.todoapp.shared_todo.domain.auth.handler;

import com.todoapp.shared_todo.domain.auth.entity.RefreshToken;
import com.todoapp.shared_todo.domain.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todoapp.shared_todo.domain.auth.repository.RefreshTokenRepository;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import com.todoapp.shared_todo.global.security.JwtProvider;
import com.todoapp.shared_todo.global.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.todoapp.shared_todo.domain.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;

    @Value("${jwt.refresh-token-expiration}") int refreshTokenExpirationTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info(">>>> [DEBUG] 핸들러 진입 성공! <<<<"); // 1. 진입 확인용
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
        String accessToken = jwtProvider.createAccessToken(userId, loginId, nickname, provider, userCode);
        String refreshToken = jwtProvider.createRefreshToken(userId, loginId, userCode);

        // [추가] 여기에 로그를 추가하세요. 콘솔창에서 바로 토큰을 볼 수 있습니다.
        log.info("생성된 Access Token: {}", accessToken);
        log.info("생성된 Refresh Token: {}", refreshToken);

        // 3. Redis에 Refresh Token 저장 (Key: "RT:loginId", Value: refreshToken)
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userLoginId(loginId)
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 4.Refresh Token-> 쿠키 설정 (Refresh Token)
        CookieUtils.addCookie(response, "refreshToken", refreshToken, refreshTokenExpirationTime);

        //  Access Token -> 리다이렉트 URL 쿼리 파라미터에 추가
        String targetUrlWithAccessToken = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        // [중요] 로그로 찍어서 서버 콘솔에서도 확인
        log.info("최종 리다이렉트 URL: {}", targetUrlWithAccessToken);

        // 5. 인증 과정에서 생긴 임시 쿠키(JSESSIONID 대체제) 삭제
        clearAuthenticationAttributes(request, response);

        // 6. 리다이렉트 (프론트엔드로)
        getRedirectStrategy().sendRedirect(request, response, targetUrlWithAccessToken);


    }

    // 타겟 URL 결정 로직 (쿠키에 저장된 redirect_uri가 있으면 거기로, 없으면 기본값으로)
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        // 프론트엔드에서 보낸 redirect_uri가 유효한지 검증하는 로직이 있으면 좋습니다 (여기서는 생략)

        // 쿠키에 없으면 기본값 (프론트엔드 홈 등)
        String targetUrl = redirectUri.orElse("http://localhost:3000/oauth2/redirect");
        return UriComponentsBuilder.fromUriString(targetUrl)
                .build().toUriString();
    }

    // 5. 인증 과정에서 생긴 임시 쿠키(JSESSIONID 대체제) 삭제
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
