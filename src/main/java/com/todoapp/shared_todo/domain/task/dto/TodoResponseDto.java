package com.todoapp.shared_todo.domain.task.dto;

import com.todoapp.shared_todo.domain.task.entity.TodoStatus;
import lombok.*;

@Getter
@Builder                
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화 대비
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더에서만 사용
public class TodoResponseDto {

    private Long todoId;
    private String content;
    private TodoStatus status;
}


