package com.todoapp.shared_todo.dto.todoLists;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TodoCreateRequestDtoTest {

    private final Validator validator;

    TodoCreateRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("TodoCreateRequestDto - content 가 비어 있으면 검증에 실패한다")
    void contentBlank_validationFail() {
        // given
        TodoCreateRequestDto dto = TodoCreateRequestDto.builder().build();

        // when
        Set<ConstraintViolation<TodoCreateRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
    }
}


