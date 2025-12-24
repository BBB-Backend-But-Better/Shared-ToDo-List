package com.todoapp.shared_todo.domain.task.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder                
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화 대비
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더에서만 사용
public class TaskResponse {

    private Long id;
    private String description;
    private Boolean completed;
    private LocalDateTime dueDate;
}

