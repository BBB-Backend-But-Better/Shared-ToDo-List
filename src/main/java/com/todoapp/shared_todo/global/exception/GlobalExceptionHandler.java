package com.todoapp.shared_todo.global.exception;

import com.todoapp.shared_todo.global.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice //모든 컨트롤러를 감시
public class GlobalExceptionHandler {

    //직접 정의한 비지니스 예외처리
    @ExceptionHandler({GeneralException.class})
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[Custom Error] Code: {}, Message: {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpstatus())
                .body(ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null));

    }

    //예상치 못한 시스템 에러
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Internal Error] ", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(errorCode.getHttpstatus())
                .body(ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null));
    }

    //dto 검증 실패 시 발생하는 예외를 잡기
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        // 3. 에러가 여러 개일 수 있지만, 첫 번째 에러 메시지만 가져옵니다. (프론트에 띄우기 위함)
        String errorMessage = errorCode.getMessage(); // 기본값은 ErrorCode의 메시지로 설정
        FieldError fieldError = bindingResult.getFieldError();

        if (fieldError != null) {
            log.warn("[Valid Error] Field: [{}], Input: [{}], Message: [{}]",
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage());
            errorMessage = fieldError.getDefaultMessage();
        } else {
            log.warn("[Valid Error] Validation failed (Global error)");
        }


        return ResponseEntity.status(errorCode.getHttpstatus())
                .body(ApiResponse.onFailure(errorCode.getCode(), errorMessage, null));
    }
}
