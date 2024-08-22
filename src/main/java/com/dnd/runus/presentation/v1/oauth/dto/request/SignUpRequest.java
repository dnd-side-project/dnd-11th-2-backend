package com.dnd.runus.presentation.v1.oauth.dto.request;

import com.dnd.runus.global.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원가입 요청 request")
public record SignUpRequest(
        @Schema(description = "소셜 로그인 타입")
        @NotNull
        SocialType socialType,
        @Schema(description = "idToken")
        @NotBlank
        String idToken,
        @Schema(description = "사용자 이름")
        @NotBlank
        String nickName
) {
}
