package com.dnd.runus.presentation.handler;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.global.exception.BaseException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.dto.response.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ExceptionRestHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorDto> handleBaseException(BaseException e) {
        return toResponseEntity(e.getType(), e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorDto> handleAuthException(AuthException e) {
        return toResponseEntity(e.getType(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleException(Exception e, HttpServletRequest request) {
        log.error(
                "Unhandled exception[{}]: {}, method: {}, uri: {}",
                e.getClass(),
                e.getMessage(),
                request.getMethod(),
                request.getRequestURI());
        return toResponseEntity(ErrorType.UNHANDLED_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNoResourceFoundException(NoResourceFoundException e) {
        return toResponseEntity(ErrorType.UNSUPPORTED_API, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDto> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return toResponseEntity(ErrorType.UNSUPPORTED_API, e.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException e) {
        return toResponseEntity(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return toResponseEntity(ErrorType.FAILED_VALIDATION, e.getMessage());
    }

    ////////////////// 요청 파라미터 예외 / 타입 불일치, Enum 매개변수 관련 예외
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        String parameterName = ex.getName(); // 파라미터 이름
        Object receivedValue = ex.getValue(); // 잘못된 값
        Class<?> requiredType = ex.getRequiredType(); // 기대하는 타입
        String requiredTypeName =
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown type"; // 기대하는 타입 이름
        String actualTypeName = receivedValue != null ? receivedValue.getClass().getSimpleName() : "null"; // 받은 타입

        String expectedTypeError = "";
        if (!requiredTypeName.equals(actualTypeName)) {
            expectedTypeError = String.format(
                    "Expected type: %s, but received type: %s; ",
                    requiredType != null ? requiredType.getSimpleName() : "unknown type",
                    receivedValue != null ? receivedValue.getClass().getName() : "null");
        }
        // requiredType이 Enum타입일 경우
        String expectedValuesError = "";
        if (requiredType != null && requiredType.isEnum()) {
            String enumValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(enumConstant -> ((Enum<?>) enumConstant).name())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");
            expectedValuesError = String.format(" Expected values: [%s]; ", enumValues);
        }
        String errorMessage = String.format(
                "Invalid value for parameter '%s' for value[%s]; %s%s",
                parameterName, receivedValue, expectedTypeError, expectedValuesError);

        log.warn("{}; {}", errorMessage, ex.getMessage());
        return toResponseEntity(ErrorType.FAILED_VALIDATION, errorMessage);
    }

    ////////////////// 직렬화 / 역직렬화 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return toResponseEntity(
                ErrorType.FAILED_VALIDATION,
                ex.getBindingResult().getAllErrors().toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return toResponseEntity(ErrorType.FAILED_PARSING, ex);
    }

    ////////////////// Database 관련 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return toResponseEntity(ErrorType.VIOLATION_OCCURRED, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        return toResponseEntity(ErrorType.VIOLATION_OCCURRED, ex);
    }

    private static ResponseEntity<ApiErrorDto> toResponseEntity(@NotNull ErrorType type, Exception exception) {
        return toResponseEntity(type, exception.getClass().getName() + ": " + exception.getMessage());
    }

    private static ResponseEntity<ApiErrorDto> toResponseEntity(@NotNull ErrorType type, String message) {
        loggingExceptionByErrorType(type, message);
        return ResponseEntity.status(type.httpStatus().value()).body(ApiErrorDto.of(type, message));
    }

    private static void loggingExceptionByErrorType(ErrorType type, String message) {
        switch (type.level()) {
            case INFO -> log.info(message);
            case DEBUG -> log.debug(message);
            case WARN -> log.warn(message);
            default -> log.error(message);
        }
    }
}
