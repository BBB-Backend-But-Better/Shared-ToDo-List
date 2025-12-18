package com.todoapp.shared_todo.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;


class UsersTest {

    @Test
    @DisplayName("빌더로 정상 생성- 성공")
    void createUsers_success() {
        //Given(준비)

        String encodedPassword = "passwordaldkfjadslkfj";

        //When(실행)
        Users users = Users.builder()
                .loginID("testuser")
                .password(encodedPassword)
                .build();

        //Then(검증)
        assertThat(users.getLoginID()).isEqualTo("testuser");
        assertThat(users.getPassword()).isEqualTo(encodedPassword);
        assertThat(users.getId()).isNotNull();

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName(("로그인 아이디가 null 또는 blank 일때"))
    void createUsers_null_blank_fail(String input) {
        assertThatThrownBy(() -> Users.builder()
                .loginID(input)
                .password("any")
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("loginID is null");

    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName(("비밀번호가  null 또는 blank 일때"))
    void createpass_null_blank_fail(String input) {
        assertThatThrownBy(() -> Users.builder()
                .loginID("and")
                .password(input)
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("pass is null");

    }


}