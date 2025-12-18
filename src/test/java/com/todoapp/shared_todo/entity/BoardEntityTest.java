package com.todoapp.shared_todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardEntityTest {

    @Test
    @DisplayName("Board 생성 시 todoLists 와 sharedUsers 컬렉션은 비어 있지만 null 이 아니다")
    void boardCollectionsInitialized() {
        Board board = new Board();

        assertThat(board.getTodoLists()).isNotNull();
        assertThat(board.getTodoLists()).isEmpty();

        assertThat(board.getSharedUsers()).isNotNull();
        assertThat(board.getSharedUsers()).isEmpty();
    }
}


