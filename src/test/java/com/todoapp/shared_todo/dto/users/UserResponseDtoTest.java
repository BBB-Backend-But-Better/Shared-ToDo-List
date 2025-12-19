package com.todoapp.shared_todo.dto.users;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class UserResponseDtoTest {

    @Test
    @DisplayName("응답 빌더 및 getter 테스트")
    void gettersTest()
    {
        UserResponseDto dto = UserResponseDto.builder()
                .loginId("test")
                .password("stast")
                .build();

        Assertions.assertThat(dto.getLoginId()).isEqualTo("test");
        Assertions.assertThat(dto.getPassword()).isEqualTo("stast");
    }

}