package com.dnd.runus.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PACKAGE)
public enum WebErrorType implements ErrorType {
    UNHANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, "직접적으로 처리되지 않은 예외, 문의해주세요"),
    FAILED_VALIDATION(BAD_REQUEST, "Request 요청에서 올바르지 않은 값이 있습니다"),
    FAILED_PARSING(BAD_REQUEST, "Request JSON body를 파싱하지 못했습니다"),
    UNSUPPORTED_API(BAD_REQUEST, "지원하지 않는 API입니다"),
    COOKIE_NOT_FOND(BAD_REQUEST, "요청에 쿠키가 필요합니다"),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
