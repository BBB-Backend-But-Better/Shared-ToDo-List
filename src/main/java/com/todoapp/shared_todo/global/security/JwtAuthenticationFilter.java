package com.todoapp.shared_todo.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if (StringUtils.hasText(token) && jwtProvider.vaildateToken(token)) {

            Claims claims = jwtProvider.getClaims(token);
            String loginId = claims.getSubject(); //로그인 정보 꺼내고
            String userCode = claims.get("userCode", String.class); //유저 코드 꺼내서 사용!

            //하지만 더미 권환 주는것이 백엔드 로직상 안전함.
            //지금은 모두 USER로 추가함.
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            CustomUserDetails principal = new CustomUserDetails(loginId, userCode, authorities);
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
