package com.todoapp.shared_todo.dto.todoLists;

import com.todoapp.shared_todo.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
public class TodoUpdateStatusRequestDto {

    private TodoStatus status;
}


