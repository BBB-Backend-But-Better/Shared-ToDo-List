package com.todoapp.shared_todo.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    /**
     *  토큰 생성
     * 페이로드: loginid, nickname, provider
     */
    public String createAccessToken(String loginId, String nickname, String provider) {
        return Jwts.builder()
                //표준 Claim
                .setSubject(loginId)

                // custom claim
                .claim("nickname", nickname)
                .claim("provider", provider)

                //시간 claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))

                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String loginId) {
        return Jwts.builder()
                //표준 Claim
                .setSubject(loginId)

                //시간 claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))

                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean vaildateToken(String token) {
        try {
            Jwts.
                    parserBuilder().
                    setSigningKey(secretKey).
                    build().
                    parseClaimsJws(token);
            return true; //문제 없으면 통과
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다."); //깨진 토큰
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다."); //유효기간 지남 -> 재발급 필요
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("알 수 없는 JWT 에러: {}", e.getMessage()); // ERROR: 서버 문제 (비상)
        }

        return false; // 예외 발생 시 무조건 false 반환
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
