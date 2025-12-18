package com.todoapp.shared_todo.dto.todoLists;

import com.todoapp.shared_todo.dto.todoLists.TodoCreateRequestDto;
import com.todoapp.shared_todo.dto.todoLists.TodoUpdateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TodoListDtoTest {

    private final Validator validator;

    TodoListDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Todo 생성 요청에서 content 가 비어 있으면 검증에 실패한다")
    void createRequestDto_contentBlank_validationFail() {
        TodoCreateRequestDto dto = TodoCreateRequestDto.builder().build();

        Set<ConstraintViolation<TodoCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Todo 수정 요청에서 content 가 비어 있으면 검증에 실패한다")
    void updateRequestDto_contentBlank_validationFail() {
        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder().build();

        Set<ConstraintViolation<TodoUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}