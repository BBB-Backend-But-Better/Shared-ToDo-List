package com.todoapp.shared_todo.domain.auth.service;

import com.todoapp.shared_todo.domain.auth.info.OAth2UserInfo;
import com.todoapp.shared_todo.domain.auth.info.impl.GoogleOAuth2UserInfo;
import com.todoapp.shared_todo.domain.auth.info.impl.NaverOAuth2UserInfo;
import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import com.todoapp.shared_todo.domain.user.entity.UsersStatus;
import com.todoapp.shared_todo.domain.user.repository.UsersRepository;
import com.todoapp.shared_todo.global.exception.ErrorCode;
import com.todoapp.shared_todo.global.exception.GeneralException;
import com.todoapp.shared_todo.global.security.CustomePrincipal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //소셜에서 사용자 정보 가져오긴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        //어떤 서비스인지  확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //규격에 맞는 UserInfo 생성 (팩토리 메서드 패턴)
        OAth2UserInfo userInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        //회원가입
        User user = saveOrUpdate(userInfo);

        //시큐리티 세션에 저장할 객체 반환

        return new CustomePrincipal(user, oAuth2User.getAttributes());

    }

    //사용자 정보 파싱
    private OAth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equals("naver")) {
            return new NaverOAuth2UserInfo(attributes);
        } else {
            log.error("지원하지 않는 소셜 로그인입니다. :" + registrationId);
            throw new GeneralException(ErrorCode.NOT_FOUND_OAUTH2);
        }
    }

    private User saveOrUpdate(OAth2UserInfo userInfo) {
        //provider + providerId 조합으로 loginId 생성
        String generatedLoginId = userInfo.getProvider() + "_" + userInfo.getProviderId();

        //db 조회
        User user = usersRepository.findByLoginId(generatedLoginId).orElse(null);


        if (user == null) {
            // 유저 코드 난수 생성(10자리)
            String generatedUserCode = RandomStringUtils.randomAlphanumeric(10);

            //엔티티 생성
            user = User.builder()
                    .loginId(generatedLoginId)
                    .nickname(userInfo.getName())
                    .password(null)
                    .userCode(generatedUserCode)
                    .providerId(userInfo.getProviderId())
                    .provider(ProviderType.valueOf(userInfo.getProvider().toUpperCase()))
                    .status(UsersStatus.CREATED)
                    .build();

            //저장
            usersRepository.save(user);

        }

        return user;
    }

}
