package com.dnd.runus.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public enum ErrorType {
    // WebErrorType
    UNHANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, "WEB_001", "직접적으로 처리되지 않은 예외, 문의해주세요"),
    FAILED_VALIDATION(BAD_REQUEST, "WEB_002", "Request 요청에서 올바르지 않은 값이 있습니다"),
    FAILED_PARSING(BAD_REQUEST, "WEB_003", "Request JSON body를 파싱하지 못했습니다"),
    UNSUPPORTED_API(BAD_REQUEST, "WEB_004", "지원하지 않는 API입니다"),
    COOKIE_NOT_FOND(BAD_REQUEST, "WEB_005", "요청에 쿠키가 필요합니다"),

    // DatabaseErrorType
    ENTITY_NOT_FOUND(NOT_FOUND, "DB_001", "해당 엔티티를 찾을 수 없습니다"),
    VIOLATION_OCCURRED(NOT_ACCEPTABLE, "DB_002", "저장할 수 없는 값입니다"),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
