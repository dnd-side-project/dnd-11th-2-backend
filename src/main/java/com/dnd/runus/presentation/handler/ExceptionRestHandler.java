package com.dnd.runus.presentation.handler;

import com.dnd.runus.global.exception.BaseException;
import com.dnd.runus.global.exception.type.WebErrorType;
import com.dnd.runus.presentation.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionRestHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException e, HttpServletRequest request) {
        log.warn(e.getMessage(), e);
        return ApiErrorResponse.toResponseEntity(e.getType(), e.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        return ApiErrorResponse.toResponseEntity(WebErrorType.UNHANDLED_EXCEPTION, e.getMessage(), request);
    }

    ////////////////// 직렬화 / 역직렬화 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn(ex.getBindingResult().getAllErrors().toString());
        return ApiErrorResponse.toResponseEntity(
                WebErrorType.FAILED_VALIDATION,
                ex.getBindingResult().getAllErrors().toString(),
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ApiErrorResponse.toResponseEntity(WebErrorType.FAILED_PARSING, ex, request);
    }
}
