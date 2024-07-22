package com.dnd.runus.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNullElse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ApiErrorDto error
) {
    public static <T> ApiResponse<?> success(T data) {
        return new ApiResponse<>(true, requireNonNullElse(data, emptyMap()), null);
    }

    public static ApiResponse<?> fail(ApiErrorDto error) {
        return new ApiResponse<>(false, null, error);
    }
}
