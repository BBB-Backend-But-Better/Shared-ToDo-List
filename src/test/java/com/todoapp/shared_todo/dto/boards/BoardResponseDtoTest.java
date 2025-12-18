package com.todoapp.shared_todo.dto.boards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardResponseDtoTest {

    @Test
    @DisplayName("BoardResponseDto 빌더로 생성 시 필드가 정상적으로 매핑된다")
    void builder_createsInstanceCorrectly() {
        // given
        Long boardId = 1L;
        String title = "title";
        String ownerLoginId = "user1";

        // when
        BoardResponseDto dto = BoardResponseDto.builder()
                .boardId(boardId)
                .title(title)
                .ownerLoginId(ownerLoginId)
                .build();

        // then
        assertThat(dto.getBoardId()).isEqualTo(boardId);
        assertThat(dto.getTitle()).isEqualTo(title);
        assertThat(dto.getOwnerLoginId()).isEqualTo(ownerLoginId);
    }
}


