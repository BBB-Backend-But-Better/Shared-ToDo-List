package com.todoapp.shared_todo.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
public class TaskUpdateRequest {

    @NotBlank(message = "Task 내용은 필수입니다.")
    @Size(min = 1, max = 100, message = "Task 내용은 1~100자 사이여야 합니다.")
    private String description;

    private LocalDateTime dueDate;
}

