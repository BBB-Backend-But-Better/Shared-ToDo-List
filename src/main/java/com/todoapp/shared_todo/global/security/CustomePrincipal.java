package com.todoapp.shared_todo.global.security;

import com.todoapp.shared_todo.domain.user.entity.ProviderType;
import com.todoapp.shared_todo.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/// 기존의 PrincipalDetails와 CustomUserDetails를 합친 최종 형태
@Getter
public class CustomePrincipal implements UserDetails, OAuth2User {

    //세션/토큰에 담길 핵심 정보
    private final String loginId; //로그인 id(Principal) or sub,email
    private final Long userId;
    private final String usercode; //유저 초대 코드
    // [추가] Provider 정보 (Google, Kakao, Local 등)
    private final ProviderType provider;
    private final Collection<? extends GrantedAuthority> authorities;

    //OAuth2 로그인 시에만 존재하는속성(jwt 검증시엔 null일 수도있음.
    private Map<String, Object> attributes;

    //[OAuth2 로그인 용 생성자] : user 엔티티와 attributes를 받음
    public CustomePrincipal(User user, Map<String,Object> attributes){
        this.userId = user.getId();
        this.loginId = user.getLoginId();
        this.usercode = user.getUserCode();
        this.provider = user.getProvider();
        this.attributes = attributes;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    //[jwt 필터 용 생성자]: 토큰에서 꺼낸 데이터만으로 생성
    public CustomePrincipal(Long userId, String loginId, String usercode, String role, ProviderType provider) {
        this.userId = userId;
        this.loginId = loginId;
        this.usercode = usercode;
        this.provider = provider;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName(){ return loginId; }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
