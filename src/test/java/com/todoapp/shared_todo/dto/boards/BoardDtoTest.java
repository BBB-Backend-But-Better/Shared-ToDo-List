package com.todoapp.shared_todo.dto.boards;

import com.todoapp.shared_todo.dto.boards.BoardCreateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BoardDtoTest {

    private final Validator validator;

    BoardDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Board 생성 요청에서 title 이 비어 있으면 검증에 실패한다")
    void createRequestDto_titleBlank_validationFail() {
        BoardCreateRequestDto dto = BoardCreateRequestDto.builder().build();

        Set<ConstraintViolation<BoardCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}