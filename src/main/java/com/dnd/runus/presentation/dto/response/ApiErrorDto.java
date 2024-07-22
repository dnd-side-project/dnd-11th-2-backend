package com.dnd.runus.presentation.dto.response;

import com.dnd.runus.global.exception.type.ErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@Schema(description = "API 애러 응답 형식")
@Getter
@ToString
@Builder(access = PRIVATE)
public class ApiErrorDto {
    @Schema(description = "응답 상태 코드", example = "400")
    private final int statusCode;
    @Schema(description = "응답 코드 이름 (디버깅용)", example = "FAILED_AUTHENTICATION")
    private final String code;
    @Schema(description = "응답 메시지 (디버깅용)", example = "인증에 실패했습니다")
    private final String message;

    public static ApiErrorDto of(
            @NotNull ErrorType type,
            String message
    ) {
        return ApiErrorDto.builder()
                .statusCode(type.httpStatus().value())
                .code(type.code())
                .message(type.message() + ", " + message)
                .build();
    }
}
