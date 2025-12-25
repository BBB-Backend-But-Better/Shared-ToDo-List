package com.todoapp.shared_todo.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckIdRequest {

    @NotBlank
    private String loginId;
}
