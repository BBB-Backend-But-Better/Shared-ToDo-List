package com.todoapp.shared_todo.dto.todoLists;

import com.todoapp.shared_todo.entity.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoUpdateStatusRequestDtoTest {

    @Test
    @DisplayName("TodoUpdateStatusRequestDto 빌더로 생성 시 status 가 설정된다")
    void builder_setsStatus() {
        // given
        TodoStatus status = TodoStatus.CHECKED;

        // when
        TodoUpdateStatusRequestDto dto = TodoUpdateStatusRequestDto.builder()
                .status(status)
                .build();

        // then
        assertThat(dto.getStatus()).isEqualTo(status);
    }
}


