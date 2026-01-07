package com.todoapp.shared_todo.domain.task.dto;

import jakarta.validation.constraints.NotNull;

public record TaskCheckRequest(
        @NotNull Long version
) {
}