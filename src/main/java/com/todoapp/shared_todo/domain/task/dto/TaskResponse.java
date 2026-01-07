package com.todoapp.shared_todo.domain.task.dto;

import com.todoapp.shared_todo.domain.task.entity.Task;
import com.todoapp.shared_todo.domain.task.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String description,
    TaskStatus status,
    LocalDateTime dueDate,
    Long version
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getVersion()
        );
    }
}