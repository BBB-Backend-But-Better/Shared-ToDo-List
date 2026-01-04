package com.todoapp.shared_todo.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //검증을 위한 도구
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //토큰 추출
        String token = resolveToken(request);

        //토큰 유효성 검사
        if (StringUtils.hasText(token) && jwtProvider.vaildateToken(token)) {

            //1. Redis 블랙리스트 확인
            String isLogout = (String) redisTemplate.opsForValue().get("blacklist:" + token);

            //로그아웃이 not null이면 에러
            if(isLogout != null){
                log.warn("로그아웃된 토큰으로 접근 시도 감지");
                throw new RuntimeException("로그아웃된 토큰입니다.");
            }

            //정상이면 토큰에서 Claims 추출
            Claims claims = jwtProvider.getClaims(token);
            Long userId = Long.valueOf(claims.getSubject()); //로그인 정보 꺼내고
            String userCode = claims.get("userCode", String.class); //유저 코드 꺼내서 사용!
            String loginId = claims.get("loginId", String.class); //유저 코드 꺼내서 사용!

            //하지만 더미 권환 주는것이 백엔드 로직상 안전함.
            //지금은 모두 USER로 추가함.
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            CustomUserDetails principal = new CustomUserDetails(userId ,loginId, userCode, authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("인증 객체 저장 완료: {}", loginId);
        }
        filterChain.doFilter(request, response);
    }

    //리퀘스트 해더에서 토큰 정보 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        //bearer 로 시작하는 뒷 토큰값만 자르기
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
