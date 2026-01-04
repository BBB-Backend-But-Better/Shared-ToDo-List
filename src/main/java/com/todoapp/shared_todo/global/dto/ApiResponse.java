package com.todoapp.shared_todo.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todoapp.shared_todo.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {
    @JsonProperty("isSuccess")
    private final boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 데이터가 null이면 JSON에서 아예 뺌
    private final T result;

    //성공시 응답
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true,"200","요청에 성공하였습니다.",result);
    }

    //실패시 응답
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    // Enum을 이용한 실패 응답
    public static <T> ApiResponse<T> onFailure(ErrorCode status) {
        return new ApiResponse<>(false, status.getCode(), status.getMessage(), null);
    }
}
