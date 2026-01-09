package com.todoapp.shared_todo.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.shared_todo.domain.board.dto.BoardCreateRequest;
import com.todoapp.shared_todo.domain.board.dto.BoardResponse;
import com.todoapp.shared_todo.domain.board.dto.BoardUpdateTitleRequest;
import com.todoapp.shared_todo.domain.board.service.BoardService;
import com.todoapp.shared_todo.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Board Controller 단위 테스트
 * MockMvc를 사용한 HTTP 요청/응답 검증
 */
@WebMvcTest(BoardController.class)
@DisplayName("Board Controller 테스트")
@SuppressWarnings("unused")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    private CustomUserDetails userDetails;

    @Test
    @DisplayName("보드 생성 성공")
    @WithMockUser
    void createBoard_success() throws Exception {
        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("New Board")
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("New Board")
                .authorId(1L)
                .completionRate(0.0f)
                .build();

        given(boardService.createBoard(eq(1L), any(BoardCreateRequest.class)))
                .willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(post("/boards")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Board"))
                .andExpect(jsonPath("$.authorId").value(1L));
    }

    @Test
    @DisplayName("보드 목록 조회 성공")
    @WithMockUser
    void getBoards_success() throws Exception {
        BoardResponse board1 = BoardResponse.builder()
                .id(1L)
                .title("Board1")
                .authorId(1L)
                .completionRate(0.0f)
                .build();

        BoardResponse board2 = BoardResponse.builder()
                .id(2L)
                .title("Board2")
                .authorId(1L)
                .completionRate(0.5f)
                .build();

        given(boardService.getBoards(1L)).willReturn(List.of(board1, board2));

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(get("/boards")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Board1"))
                .andExpect(jsonPath("$[1].title").value("Board2"));
    }

    @Test
    @DisplayName("보드 단건 조회 성공")
    @WithMockUser
    void getBoard_success() throws Exception {
        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("Test Board")
                .authorId(1L)
                .completionRate(0.0f)
                .build();

        given(boardService.getBoard(1L, 1L)).willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(get("/boards/{boardId}", 1L)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Board"));
    }

    @Test
    @DisplayName("보드 제목 수정 성공")
    @WithMockUser
    void updateBoardTitle_success() throws Exception {
        BoardUpdateTitleRequest request = BoardUpdateTitleRequest.builder()
                .title("Updated Title")
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("Updated Title")
                .authorId(1L)
                .completionRate(0.0f)
                .build();

        given(boardService.updateBoardTitle(eq(1L), eq(1L), any(String.class)))
                .willReturn(response);

        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(put("/boards/{boardId}/title", 1L)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("보드 삭제 성공")
    @WithMockUser
    void deleteBoard_success() throws Exception {
        userDetails = new CustomUserDetails(1L, "testuser", "USER001", Collections.emptyList());

        mockMvc.perform(delete("/boards/{boardId}", 1L)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }
}

