package com.todoapp.shared_todo.dto.boards;

import lombok.*;

@Getter
@Builder                
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화 대비
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더에서만 사용
public class BoardResponseDto {

    private Long boardId;
    private String title;
    private String ownerLoginId;
}


