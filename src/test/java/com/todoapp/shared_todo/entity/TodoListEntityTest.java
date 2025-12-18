package com.todoapp.shared_todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoListEntityTest {

    @Test
    @DisplayName("TodoList 기본 생성 시 상태는 UNCHECKED 이다")
    void defaultStatusIsUnchecked() {
        TodoList todoList = new TodoList();

        assertThat(todoList.getStatus()).isEqualTo(TodoStatus.UNCHECKED);
    }
}


