package com.todoapp.shared_todo.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
public class TaskUpdateRequest {

    @NotBlank(message = "Task 설명은 필수입니다.")
    @Size(min = 1, message = "Task 설명은 최소 1자 이상이어야 합니다.")
    private String description;
}

