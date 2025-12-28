package com.todoapp.shared_todo.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String loginId; //로그인 id(Principal)
    private final Long userId;
    private final String usercode; //유저 초대 코드

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String loginId, String usercode, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.loginId = loginId;
        this.usercode = usercode;
        this.authorities = authorities;
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

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public String getPassword() {
        return null;
    }

}
