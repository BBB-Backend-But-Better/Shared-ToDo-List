package com.todoapp.shared_todo.domain.auth.info;

public interface OAth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
}
