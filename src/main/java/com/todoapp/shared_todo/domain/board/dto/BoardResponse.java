package com.todoapp.shared_todo.domain.board.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화 대비
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더에서만 사용
public class BoardResponse {

    private Long id;
    private String title;
    private Long authorId;
    private Float completionRate;
}
