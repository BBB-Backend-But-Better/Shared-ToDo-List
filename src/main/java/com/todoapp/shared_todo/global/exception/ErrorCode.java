package com.todoapp.shared_todo.global.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* --- 공통 (Common) [cite: 54] --- */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),

    /* --- 인증/계정 (Auth & Account) [cite: 60] --- */
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요."),

    /* --- 토큰 (Token/JWT) [cite: 66] --- */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 제공되지 않았습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 리프레시 토큰입니다."),

    /* --- 보드 & 테스크 (Board & Task) [cite: 72] --- */
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 보드입니다."),
    NOT_BOARD_OWNER(HttpStatus.FORBIDDEN, "보드의 소유자가 아닙니다."),
    NOT_BOARD_MEMBER(HttpStatus.FORBIDDEN, "해당 보드의 멤버가 아닙니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 할 일(Task)입니다."),
    TASK_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 할 일입니다."),

    /* --- 초대 (Invitation) [cite: 78] --- */
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대장이 존재하지 않거나 만료되었습니다."),
    INVITATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "이미 초대된 사용자입니다."),
    ALREADY_BOARD_MEMBER(HttpStatus.CONFLICT, "이미 보드에 참여 중인 멤버입니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "자기 자신은 초대할 수 없습니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "초대장 유효 기간이 만료되었습니다.");

    private final HttpStatus status;
    private final String message;
}