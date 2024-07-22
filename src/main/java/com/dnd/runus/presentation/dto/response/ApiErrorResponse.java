package com.dnd.runus.presentation.dto.response;

import com.dnd.runus.global.exception.type.ErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import static lombok.AccessLevel.PRIVATE;

@Schema(description = "API 애러 응답 형식")
@Getter
@ToString
@Builder(access = PRIVATE)
public class ApiErrorResponse {
    @Schema(description = "요청 경로", example = "/api/v1/auth/login")
    private final String path;
    @Schema(description = "응답 상태 코드", example = "400")
    private final int statusCode;
    @Schema(description = "응답 상태 코드 이름", example = "BAD_REQUEST")
    private final String statusName;
    @Schema(description = "응답 코드 이름", example = "FAILED_AUTHENTICATION")
    private final String codeName;
    @Schema(description = "응답 메시지", example = "인증에 실패했습니다")
    private final String message;

    public static ResponseEntity<ApiErrorResponse> toResponseEntity(
            @NotNull ErrorType errorCode,
            Exception exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(errorCode.httpStatus().value())
                .body(of(errorCode, exception.getMessage(), request));
    }

    public static ResponseEntity<ApiErrorResponse> toResponseEntity(
            @NotNull ErrorType errorCode,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(errorCode.httpStatus().value())
                .body(of(errorCode, message, request));
    }

    private static ApiErrorResponse of(
            @NotNull ErrorType errorCode,
            String message,
            HttpServletRequest request
    ) {
        return ApiErrorResponse.builder()
                .path(request.getServletPath())
                .statusCode(errorCode.httpStatus().value())
                .statusName(errorCode.httpStatus().name())
                .codeName(errorCode.name())
                .message(errorCode.message() + ", " + message)
                .build();
    }
}
