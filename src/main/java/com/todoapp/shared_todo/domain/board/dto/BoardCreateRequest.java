package com.todoapp.shared_todo.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class BoardCreateRequest {

    @NotBlank(message = "보드 제목은 필수입니다.")
    @Size(min = 1, max = 50, message = "보드 제목은 1~50자 사이여야 합니다.")
    @Schema(description = "보드 생성", example = "안녕하세요 이것은 보드 생성입니다!")
    private String title;
}
