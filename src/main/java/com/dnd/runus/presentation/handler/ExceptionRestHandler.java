package com.dnd.runus.presentation.handler;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.global.exception.BaseException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.dto.response.ApiErrorDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionRestHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorDto> handleBaseException(BaseException e) {
        log.warn(e.getMessage(), e);
        return toResponseEntity(e.getType(), e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorDto> handleAuthException(AuthException e) {
        log.warn(e.getMessage(), e);
        return toResponseEntity(e.getType(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return toResponseEntity(ErrorType.UNHANDLED_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNoResourceFoundException(NoResourceFoundException e) {
        return toResponseEntity(ErrorType.UNSUPPORTED_API, e.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException e) {
        return toResponseEntity(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
    }

    ////////////////// 직렬화 / 역직렬화 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getBindingResult().getAllErrors().toString());
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
        log.warn(ex.getMessage(), ex);
        return toResponseEntity(ErrorType.VIOLATION_OCCURRED, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(ex.getMessage(), ex);
        return toResponseEntity(ErrorType.VIOLATION_OCCURRED, ex);
    }

    private static ResponseEntity<ApiErrorDto> toResponseEntity(@NotNull ErrorType type, Exception exception) {
        return toResponseEntity(type, exception.getMessage());
    }

    private static ResponseEntity<ApiErrorDto> toResponseEntity(@NotNull ErrorType type, String message) {
        return ResponseEntity.status(type.httpStatus().value()).body(ApiErrorDto.of(type, message));
    }
}
