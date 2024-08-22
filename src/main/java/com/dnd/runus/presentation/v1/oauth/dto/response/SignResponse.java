package com.dnd.runus.presentation.v1.oauth.dto.response;


import com.dnd.runus.auth.token.dto.AuthTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 및 회원가입 응답 DTO")
public record SignResponse(
    @Schema(description = "사용자 닉네임")
    String nickName,
    @Schema(description = "사용자 이메일")
    String email,
    @Schema(description = "엑세스 토큰")
    String accessToken,
    //todo refresh token 구현 되면
    @Schema(description = "리프레시토큰(아직 리프레시 토큰 구현이 아직 안되어서 발급하면 'refreshToken'으로 리턴될 거에요.")
    String refreshToken
) {

    public static SignResponse from(String nickName, String email, AuthTokenDto tokenDto) {
        return new SignResponse(
            nickName,
            email,
            tokenDto.accessToken(),
            "refreshToken");
    }
}
