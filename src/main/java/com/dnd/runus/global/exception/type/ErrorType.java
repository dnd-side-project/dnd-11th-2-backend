package com.dnd.runus.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PRIVATE;
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
    INVALID_BASE64(BAD_REQUEST, "WEB_006", "잘못된 Base64 문자열입니다"),

    // AuthErrorType
    FAILED_AUTHENTICATION(UNAUTHORIZED, "AUTH_001", "인증에 실패하였습니다"),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다"),
    EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, "AUTH_003", "만료된 토큰입니다"),
    MALFORMED_ACCESS_TOKEN(UNAUTHORIZED, "AUTH_004", "잘못된 형식의 토큰입니다"),
    TAMPERED_ACCESS_TOKEN(UNAUTHORIZED, "AUTH_005", "변조된 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(UNAUTHORIZED, "AUTH_006", "지원하지 않는 JWT 토큰입니다"),
    UNSUPPORTED_SOCIAL_TYPE(UNAUTHORIZED, "AUTH_007", "지원하지 않는 소셜 타입입니다."),

    // OauthErrorType
    USER_NOT_FOUND(NOT_FOUND, "OAUTH_001", "존재하지 않은 사용자 입니다."),

    // DatabaseErrorType
    ENTITY_NOT_FOUND(NOT_FOUND, "DB_001", "해당 엔티티를 찾을 수 없습니다"),
    VIOLATION_OCCURRED(NOT_ACCEPTABLE, "DB_002", "저장할 수 없는 값입니다"),

    // TimeErrorType
    START_AFTER_END(BAD_REQUEST, "TIME_001", "시작 시간이 종료 시간보다 빨라야 합니다"),

    // RunningErrorType
    ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES(BAD_REQUEST, "RUNNING_001", "경로는 최소 2개의 좌표를 가져야 합니다"),
    CHALLENGE_MODE_WITH_PERSONAL_GOAL(BAD_REQUEST, "RUNNING_002", "챌린지 모드에서는 개인 목표를 설정할 수 없습니다."),
    GOAL_MODE_WITH_CHALLENGE_ID(BAD_REQUEST, "RUNNING_003", "개인 목표 모드에서는 챌린지 ID를 설정할 수 없습니다."),
    GOAL_TIME_AND_DISTANCE_BOTH_EXIST(BAD_REQUEST, "RUNNING_004", "개인 목표 시간과 거리 중 하나만 설정해야 합니다."),

    // WeatherErrorType
    WEATHER_API_ERROR(INTERNAL_SERVER_ERROR, "WEATHER_001", "날씨 API 호출 중 오류가 발생했습니다"),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
