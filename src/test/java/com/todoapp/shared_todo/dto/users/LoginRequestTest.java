package com.todoapp.shared_todo.dto.users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("login 정상 입력 - 검증 통과")
    void LoginRequestDtoTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test", "test");
        Set<ConstraintViolation<LoginRequestDto>> validate = validator.validate(loginRequestDto);

        Assertions.assertTrue(validate.isEmpty());

    }

    @Test
    @DisplayName("login이 blank ")


}