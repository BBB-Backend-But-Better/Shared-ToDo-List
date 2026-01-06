package com.todoapp.shared_todo.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtils {
    /**
     * 요청(Request)에서 특정 이름의 쿠키를 찾아서 반환합니다.
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 응답(Response)에 새로운 쿠키를 추가합니다.
     * (OAuth2 인증 정보 저장용이므로 보통 HttpOnly=true, Path=/ 로 설정합니다)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 자바스크립트 접근 차단 (보안)
        cookie.setMaxAge(maxAge);

        // 로컬 환경(http)에서도 동작해야 하므로 setSecure는 배포 환경(https) 여부에 따라 유동적으로 설정하거나
        // 앞서 작성하신 코드처럼 false로 두었다가 운영 시 true로 바꾸셔도 됩니다.
        // cookie.setSecure(true);

        response.addCookie(cookie);
    }

    /**
     * 쿠키를 삭제합니다. (MaxAge를 0으로 설정하여 덮어씌움)
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * 객체(Object) -> 문자열(String) 변환 (직렬화)
     * 쿠키 값에는 공백이나 특수문자가 들어갈 수 없으므로 URL-Safe Base64로 인코딩합니다.
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * 문자열(String) -> 객체(Object) 변환 (역직렬화)
     * 쿠키에 저장된 값을 다시 자바 객체로 복구합니다.
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())
        ));
    }
}
