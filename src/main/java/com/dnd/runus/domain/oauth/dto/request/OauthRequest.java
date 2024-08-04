package com.dnd.runus.domain.oauth.dto.request;

import com.dnd.runus.global.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 및 회원가입 요청 DTO")
public record OauthRequest(
    @Schema(
        description = "소셜 로그인 타입",
        type = "string",
        example = "APPLE"
    )
    @NotNull
    SocialType socialType,
    @Schema(
        description = "idToken"
    )
    @NotBlank
    String idToken,
    @Schema(
        description = "사용자 email(최초 로그인 이후 해당 값을 애플에서 제공 안 한다고 들었는데, 만약 최초 로그인 이후 제공 안한다면 알려 주세요!)",
        example = "xxxx@yyyy.com"
    )
    @NotBlank
    @Email
    String email,
    @Schema(
        description = "사용자 이름(최초 로그인 이후 해당 값을 애플에서 제공 안 한다고 들었는데, 만약 최초 로그인 이후 제공 안한다면 알려 주세요!)"
    )
    @NotBlank
    String nickName
){
}
