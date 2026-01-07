package com.todoapp.shared_todo.domain.auth.info.impl;

import com.todoapp.shared_todo.domain.auth.info.OAth2UserInfo;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAth2UserInfo {

    private final Map<String, Object> attributes; // Oauth2User에서 넘어온 전체 속성

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        if (attributes.containsKey("response")) {
            this.attributes = (Map<String, Object>) attributes.get("response");
        } else {
            this.attributes = attributes;
        } //미리 꺼내와서 변환
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
