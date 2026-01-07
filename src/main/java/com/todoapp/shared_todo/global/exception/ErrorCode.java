package com.todoapp.shared_todo.global.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* --- 공통 (Common) [cite: 54] --- */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001","입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002","허용되지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_003","서버 내부 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_004","접근 권한이 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_005","요청하신 리소스를 찾을 수 없습니다."),

    /* ----OAtuth2 에러 ------*/
    NOT_FOUND_OAUTH2(HttpStatus.NOT_FOUND,"OAUTH2_001","지원하지 않는 소셜 로그인입니다."),


    /* --- 인증/계정 (Auth & Account) [cite: 60] --- */
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "AUTH_001","이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "AUTH_002","이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_003","비밀번호 형식이 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_004","존재하지 않는 회원입니다."),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_005","아이디 또는 비밀번호를 확인해주세요."),
    /* --- 토큰 (Token/JWT) [cite: 66] --- */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_001","유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_002","만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_003","지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_004","토큰이 제공되지 않았습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_005","유효하지 않거나 만료된 리프레시 토큰입니다."),
    NOT_FOUND_REDIS(HttpStatus.NOT_FOUND,"JWT_TOKEN_006","Redis에 존재 하지 않는 리플래시 토큰입니다."),

    /* --- 보드 & 테스크 (Board & Task) [cite: 72] --- */
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_TASK_001","존재하지 않는 보드입니다."),
    NOT_BOARD_OWNER(HttpStatus.FORBIDDEN, "BOARD_TASK_002","보드의 소유자가 아닙니다."),
    NOT_BOARD_MEMBER(HttpStatus.FORBIDDEN, "BOARD_TASK_003","해당 보드의 멤버가 아닙니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_TASK_004","존재하지 않는 할 일(Task)입니다."),
    TASK_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "BOARD_TASK_005","이미 완료된 할 일입니다."),

    /* --- 초대 (Invitation) [cite: 78] --- */
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITAION_001","초대장이 존재하지 않거나 만료되었습니다."),
    INVITATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITAION_002","존재하지 않는 사용자입니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "INVITAION_003","이미 초대된 사용자입니다."),
    ALREADY_BOARD_MEMBER(HttpStatus.CONFLICT, "INVITAION_004","이미 보드에 참여 중인 멤버입니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "INVITAION_005","자기 자신은 초대할 수 없습니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "INVITAION_006","초대장 유효 기간이 만료되었습니다.");

    private final HttpStatus httpstatus;
    private final String code; //프론트가 볼 메세지
    private final String message; //사용자가 볼 메서지
}