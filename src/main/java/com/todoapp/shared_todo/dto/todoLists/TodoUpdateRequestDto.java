package com.todoapp.shared_todo.dto.todoLists;

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
public class TodoUpdateRequestDto {

    @NotBlank(message = "Todo 내용은 필수입니다.")
    @Size(min = 1, max = 100, message = "Todo 내용은 1 ~ 100자 사이여야 합니다.")
    private String content;
}


