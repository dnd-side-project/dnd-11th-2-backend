package com.dnd.runus.domain.oauth.dto.response;


import com.dnd.runus.auth.token.dto.AuthTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 및 회원가입 응답 DTO")
public record TokenResponse(
    @Schema(description = "엑세스 토큰")
    String accessToken,
    //todo refresh token 구현 되면
    @Schema(description = "리프레시토큰(아직 리프레시 토큰 구현이 아직 안되어서 발급하면 'refreshToken'으로 리턴될 거에요.")
    String refreshToken,
    @Schema(
        description = "토큰 타입",
        example = "Bearer "
    )
    String grantType
) {

    public static TokenResponse from(AuthTokenDto tokenDto) {
        return new TokenResponse(tokenDto.accessToken(), "refreshToken", tokenDto.type());
    }
}
