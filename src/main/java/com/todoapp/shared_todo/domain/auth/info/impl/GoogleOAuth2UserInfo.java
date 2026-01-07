package com.todoapp.shared_todo.domain.auth.info.impl;

import com.todoapp.shared_todo.domain.auth.info.OAth2UserInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAth2UserInfo {

    private Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}

