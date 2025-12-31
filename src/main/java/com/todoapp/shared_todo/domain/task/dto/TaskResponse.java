package com.todoapp.shared_todo.domain.task.dto;

import com.todoapp.shared_todo.domain.task.entity.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder                
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화 대비
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더에서만 사용
public class TaskResponse {

    private Long id;
    private String description;
    // 기존 코드: private Boolean completed;
    private TaskStatus status;
    private LocalDateTime dueDate;
}

