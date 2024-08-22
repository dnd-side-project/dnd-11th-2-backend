package com.dnd.runus.presentation.v1.oauth.dto.request;

import com.dnd.runus.global.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 요청 request")
public record SignInRequest(
        @Schema(description = "소셜 로그인 타입")
        @NotNull
        SocialType socialType,
        @Schema(description = "idToken")
        @NotBlank
        String idToken
) {
}
