package com.todoapp.shared_todo.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


public record CheckLoginidRequest(
        @NotBlank
        String loginId) {
}
