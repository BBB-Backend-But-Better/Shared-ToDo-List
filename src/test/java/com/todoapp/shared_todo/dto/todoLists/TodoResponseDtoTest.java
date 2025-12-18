package com.todoapp.shared_todo.dto.todoLists;

import com.todoapp.shared_todo.entity.TodoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoResponseDtoTest {

    @Test
    @DisplayName("TodoResponseDto 빌더로 생성 시 필드가 정상적으로 매핑된다")
    void builder_createsInstanceCorrectly() {
        // given
        Long id = 1L;
        String content = "test";
        TodoStatus status = TodoStatus.CHECKED;

        // when
        TodoResponseDto dto = TodoResponseDto.builder()
                .todoId(id)
                .content(content)
                .status(status)
                .build();

        // then
        assertThat(dto.getTodoId()).isEqualTo(id);
        assertThat(dto.getContent()).isEqualTo(content);
        assertThat(dto.getStatus()).isEqualTo(status);
    }
}


