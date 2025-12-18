package com.todoapp.shared_todo.dto.boards;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BoardCreateRequestDtoTest {

    private final Validator validator;

    BoardCreateRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("BoardCreateRequestDto - title 이 비어 있으면 검증에 실패한다")
    void titleBlank_validationFail() {
        // given
        BoardCreateRequestDto dto = BoardCreateRequestDto.builder().build();

        // when
        Set<ConstraintViolation<BoardCreateRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
    }
}


