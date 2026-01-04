package com.todoapp.shared_todo.domain.auth.service;

import com.todoapp.shared_todo.domain.auth.dto.request.CheckLoginidRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.LoginRequest;
import com.todoapp.shared_todo.domain.auth.dto.request.SignupRequest;
import com.todoapp.shared_todo.domain.auth.dto.TokenDto;
import com.todoapp.shared_todo.domain.auth.dto.response.AvailableResponse;
import com.todoapp.shared_todo.domain.auth.entity.RefreshToken;
import com.todoapp.shared_todo.domain.auth.repository.RefreshTokenRepository;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import com.todoapp.shared_todo.global.exception.ErrorCode;
import com.todoapp.shared_todo.global.exception.GeneralException;
import com.todoapp.shared_todo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //회원가입
    @Transactional
    public void signup(SignupRequest request) {

        //아이디 중복검사.
        if (usersRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 유저 코드 난수 생성(10자리)
        String generatedUserCode = RandomStringUtils.randomAlphanumeric(10);

        //엔티티 생성
        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .userCode(generatedUserCode)
                .build();

        //저장
        usersRepository.save(user);
    }

    //로그인
    @Transactional
    public TokenDto login(LoginRequest request) {

        //아이디검증
        User user = usersRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        //pw 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorCode.LOGIN_FAILED);
        }
        //토큰 생성
        return issueTokenTdo(user);
    }

    //로그아웃
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        //1. 액서스 토큰 남은 유효시간 계산, 블랙리스트 등록
        Long expiredToken = jwtProvider.getExpiredToken(accessToken);
        if(expiredToken > 0){
            redisTemplate.opsForValue().set("blacklist:"+ accessToken, "logout:", expiredToken, TimeUnit.MICROSECONDS);
        }
        
        //리플래시 토큰 처리 하는데, 거기서 id를 뽑아서 삭제
        if(jwtProvider.vaildateToken(refreshToken)){

            String userLoginId = jwtProvider.getClaims(refreshToken).get("loginId", String.class);
            refreshTokenRepository.deleteById(userLoginId);
        }else{
            throw new GeneralException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }


    //아이디 중복 체크(API용)
    public AvailableResponse checkLoginIdDuplicate(CheckLoginidRequest checkLoginidRequest) {
        boolean exists = usersRepository.existsByLoginId(checkLoginidRequest.loginId());

        if (exists) {
            return new AvailableResponse(false, "이미 사용중인 아이디 입니다.");
        } else {
            return new AvailableResponse(true, "사용 가능한 아이디입니다.");
        }
    }

    //리플래시 토큰으로 검증하고, 토큰 재발급
    //API요청은 엑서스 토큰으로 확인
    @Transactional
    public TokenDto reissue(String requestRefreshToken) {
        //들어온 리프레시 토큰 자체의 유효성 검사 (위조, 만료 등)
        if (!jwtProvider.vaildateToken(requestRefreshToken)) {
            throw new GeneralException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String loginId = jwtProvider.getClaims(requestRefreshToken).get("loginId", String.class);

        //Redis에서 토큰 조회(존재하는지)
        RefreshToken redisToken = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND_REDIS));

        //Redis에 저장된 토큰 vs 요청온 토큰 일치 여부 확인
        if (!redisToken.getToken().equals(requestRefreshToken)) {
            throw new GeneralException(ErrorCode.EXPIRED_TOKEN);
        }

        //토큰에서 유저조회(유저가 맞는지 확인)
        User user = usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        //기존 토큰은 삭제
        refreshTokenRepository.delete(redisToken);

        //새로운 토큰 재발급
        return issueTokenTdo(user);
    }


    // ========== [핵심] 공통 로직 추출 ==========
    private TokenDto issueTokenTdo(User user) {
        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getProvider(),
                user.getUserCode());

        String newRefreshToken = jwtProvider.createRefreshToken(
                user.getId(),
                user.getLoginId(),
                user.getUserCode());

        // 7. 새 리프레시 토큰 저장
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .userLoginId(user.getLoginId())
                .token(newRefreshToken)
                .build();

        refreshTokenRepository.save(newRefreshTokenEntity);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

}
