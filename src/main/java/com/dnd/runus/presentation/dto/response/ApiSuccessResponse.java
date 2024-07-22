package com.dnd.runus.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNullElse;

@Schema(description = "API 성공 응답 형식")
public record ApiSuccessResponse<T>(
        @Schema(description = "응답 상태 코드", example = "200")
        int statusCode,
        @Schema(description = "응답 데이터")
        T data
) {
    public static <T> ApiSuccessResponse<?> of(int statusCode, T data) {
        return new ApiSuccessResponse<>(statusCode, requireNonNullElse(data, emptyMap()));
    }
}
