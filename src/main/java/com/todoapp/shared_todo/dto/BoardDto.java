package com.todoapp.shared_todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    // 보드 생성 요청
    public static class CreateRequestDto {

        @NotBlank(message = "보드 제목은 필수입니다.")
        @Size(min = 1, max = 50, message = "보드 제목은 1 ~ 50자 사이여야 합니다.")
        private String title;
    }

    // 보드 단건 응답
    public static class ResponseDto {
        private Long boardId;
        private String title;
        private String ownerLoginId;
    }
}