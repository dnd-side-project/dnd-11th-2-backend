package com.dnd.runus.presentation.v1.oauth.dto.response;


import com.dnd.runus.auth.token.dto.AuthTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 및 회원가입 응답 DTO")
public record SignResponse(
    @Schema(description = "사용자 닉네임")
    String nickname,
    @Schema(description = "사용자 이메일")
    String email,
    @Schema(description = "엑세스 토큰")
    String accessToken,
    @Schema(description = "리프레시 토큰")
    String refreshToken
) {

    public static SignResponse from(String nickname, String email, AuthTokenDto tokenDto) {
        return new SignResponse(
            nickname,
            email,
            tokenDto.accessToken(),
            tokenDto.refreshToken());
    }
}
