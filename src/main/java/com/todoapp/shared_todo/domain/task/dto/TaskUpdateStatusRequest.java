package com.todoapp.shared_todo.domain.task.dto;

import com.todoapp.shared_todo.domain.task.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
public class TaskUpdateStatusRequest {

    // 기존 코드: private Boolean completed;
    private TaskStatus status;
}

