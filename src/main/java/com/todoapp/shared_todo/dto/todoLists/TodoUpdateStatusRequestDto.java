package com.todoapp.shared_todo.dto.todoLists;

import com.todoapp.shared_todo.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      // JSON 역직렬화 필수
@AllArgsConstructor     // 테스트 편의 + 빠른 생성
@Builder                // 부분 필드 테스트 + 가독성
public class TodoUpdateStatusRequestDto {

    private TodoStatus status;
}


