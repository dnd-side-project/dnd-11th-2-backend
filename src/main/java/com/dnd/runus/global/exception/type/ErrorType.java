package com.dnd.runus.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import static com.dnd.runus.global.exception.type.ErrorType.Level.*;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public enum ErrorType {
    // WebErrorType
    UNHANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, ERROR, "WEB_001", "직접적으로 처리되지 않은 예외, 문의해주세요"),
    FAILED_VALIDATION(BAD_REQUEST, WARN, "WEB_002", "Request 요청에서 올바르지 않은 값이 있습니다"),
    FAILED_PARSING(BAD_REQUEST, WARN, "WEB_003", "Request JSON body를 파싱하지 못했습니다"),
    UNSUPPORTED_API(BAD_REQUEST, DEBUG, "WEB_004", "지원하지 않는 API입니다"),
    COOKIE_NOT_FOND(BAD_REQUEST, DEBUG, "WEB_005", "요청에 쿠키가 필요합니다"),
    INVALID_BASE64(BAD_REQUEST, DEBUG, "WEB_006", "잘못된 Base64 문자열입니다"),

    // AuthErrorType
    FAILED_AUTHENTICATION(UNAUTHORIZED, INFO, "AUTH_001", "인증에 실패하였습니다"),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, DEBUG, "AUTH_002", "유효하지 않은 토큰입니다"),
    EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, DEBUG, "AUTH_003", "만료된 토큰입니다"),
    MALFORMED_ACCESS_TOKEN(UNAUTHORIZED, DEBUG, "AUTH_004", "잘못된 형식의 토큰입니다"),
    TAMPERED_ACCESS_TOKEN(UNAUTHORIZED, DEBUG, "AUTH_005", "변조된 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(UNAUTHORIZED, DEBUG, "AUTH_006", "지원하지 않는 JWT 토큰입니다"),
    UNSUPPORTED_SOCIAL_TYPE(UNAUTHORIZED, DEBUG, "AUTH_007", "지원하지 않는 소셜 타입입니다"),
    INVALID_CREDENTIALS(UNAUTHORIZED, DEBUG, "AUTH_008", "해당 사용자의 정보가 없거나 일치하지 않아 처리할 수 없습니다"),

    // OauthErrorType
    SOCIAL_MEMBER_NOT_FOUND(NOT_FOUND, DEBUG, "OAUTH_001", "찾을 수 없는 소셜 회원입니다"),
    EMAIL_NOT_FOUND_IN_ID_TOKEN(BAD_REQUEST, DEBUG, "OAUTH_002", "ID 토큰 필드에 이메일이 없습니다"),

    // DatabaseErrorType
    ENTITY_NOT_FOUND(NOT_FOUND, DEBUG, "DB_001", "해당 엔티티를 찾을 수 없습니다"),
    VIOLATION_OCCURRED(NOT_ACCEPTABLE, ERROR, "DB_002", "저장할 수 없는 값입니다"),

    // TimeErrorType
    START_AFTER_END(BAD_REQUEST, DEBUG, "TIME_001", "시작 시간이 종료 시간보다 빨라야 합니다"),

    // RunningErrorType
    ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES(BAD_REQUEST, DEBUG, "RUNNING_001", "경로는 최소 2개의 좌표를 가져야 합니다"),
    CHALLENGE_MODE_WITH_PERSONAL_GOAL(BAD_REQUEST, DEBUG, "RUNNING_002", "챌린지 모드에서는 개인 목표를 설정할 수 없습니다"),
    GOAL_MODE_WITH_CHALLENGE_ID(BAD_REQUEST, DEBUG, "RUNNING_003", "개인 목표 모드에서는 챌린지 ID를 설정할 수 없습니다"),
    GOAL_TIME_AND_DISTANCE_BOTH_EXIST(BAD_REQUEST, DEBUG, "RUNNING_004", "개인 목표 시간과 거리 중 하나만 설정해야 합니다"),
    GOAL_VALUES_REQUIRED_IN_GOAL_MODE(BAD_REQUEST, DEBUG, "RUNNING_005", "개인 목표 모드에서, 개인 목표 달성값은 필수 잆니다."),
    CHALLENGE_VALUES_REQUIRED_IN_CHALLENGE_MODE(BAD_REQUEST, DEBUG, "RUNNING_006", "챌린지 모드에서, 챌린지 달성값은 필수 입니다."),

    // WeatherErrorType
    WEATHER_API_ERROR(SERVICE_UNAVAILABLE, WARN, "WEATHER_001", "날씨 API 호출 중 오류가 발생했습니다"),
    ;
    private final HttpStatus httpStatus;
    private final Level level;
    private final String code;
    private final String message;

    public enum Level {
        INFO,
        DEBUG,
        WARN,
        ERROR
    }
}
