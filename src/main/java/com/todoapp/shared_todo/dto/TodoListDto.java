package com.todoapp.shared_todo.dto;

import com.todoapp.shared_todo.entity.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoListDto {

    // Todo 생성 요청
    public static class CreateRequestDto {

        @NotBlank(message = "Todo 내용은 필수입니다.")
        @Size(min = 1, max = 100, message = "Todo 내용은 1 ~ 100자 사이여야 합니다.")
        private String content;
    }

    // Todo 수정(내용 변경) 요청
    public static class UpdateRequestDto {

        @NotBlank(message = "Todo 내용은 필수입니다.")
        @Size(min = 1, max = 100, message = "Todo 내용은 1 ~ 100자 사이여야 합니다.")
        private String content;
    }

    // 상태 변경 요청 (CHECKED / UNCHECKED)
    public static class UpdateStatusRequestDto {
        private TodoStatus status;
    }

    // Todo 응답
    public static class ResponseDto {
        private Long todoId;
        private String content;
        private TodoStatus status;
    }
}