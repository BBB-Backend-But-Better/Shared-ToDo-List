package com.todoapp.shared_todo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

/**
 * 이제 런타임 에러를 상속 받은 비지니스 에러 처리기를 사요하면됨.
 * new RuntimeException 대신 new GeneralException를 사용하고, 에러 코드를 지정하면된.
 * */
public class GeneralException extends RuntimeException {
    private final ErrorCode errorCode;
}
