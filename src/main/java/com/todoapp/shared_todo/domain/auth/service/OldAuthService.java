/*
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
import com.todoapp.shared_todo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OldAuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //회원가입
    @Transactional
    public void signup(SignupRequest request) {

        //아이디 중복검사.
        if (usersRepository.existsByLoginId(request.loginId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
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
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));

        //pw 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        //토큰 생성
        return issueTokenTdo(user);
    }

    //로그아웃
    @Transactional
    public void logout(String requestRefreshToken) {
        refreshTokenRepository.findByToken(requestRefreshToken)
                .ifPresent(token -> {
                    token.revoke();
                });
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

    //토큰 재발급
    @Transactional
    public TokenDto reissue(String requestRefreshToken) {
        //토큰 유효성 검사(JWT provider)
        if (!jwtProvider.vaildateToken(requestRefreshToken)) {
            throw new RuntimeException("유효하지 않은 리플래시 토큰입니다.");
        }

        //db에서 토큰 조회(존재하는지)
        RefreshToken oldToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("DB에 존재 하지 않는 리플래시 토큰입니다."));

        //만료 여부 확인
        if (oldToken.isRevoked()) {
            throw new RuntimeException("이미 폐기된(로그아웃된) 리플래시 토큰입니다.");
        }

        //토큰에서 유저조회(유저가 맞는지 확인)
        User user = usersRepository.findByLoginId(oldToken.getUserLoginId())
                .orElseThrow(() -> new RuntimeException(" 유저를 찾을 수 없습니다."));

        oldToken.revoke();

        //새로운 토큰 재발급
        return issueTokenTdo(user);
    } //푸후에 오래된 토큰 삭제해야됨.


    // ========== [핵심] 공통 로직 추출 ==========
    private TokenDto issueTokenTdo(User user) {
        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getProvider(),
                user.getUserCode());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        // 7. 새 리프레시 토큰 저장
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .userLoginId(user.getLoginId())
                .token(newRefreshToken)
                .revoked(false) // 새 토큰은 쌩쌩함
                .build();

        refreshTokenRepository.save(newRefreshTokenEntity);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

}
*/
