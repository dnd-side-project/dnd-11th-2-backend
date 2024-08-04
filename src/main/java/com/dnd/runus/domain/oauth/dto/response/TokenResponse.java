package com.dnd.runus.domain.oauth.dto.response;


import com.dnd.runus.auth.token.dto.AuthTokenDto;

public record TokenResponse(
    String accessToken,
//todo refresh token 구현 되면
   String refreshToken,
    String grantType
) {

    public static TokenResponse from(AuthTokenDto tokenDto) {
        return new TokenResponse(tokenDto.accessToken(), "refreshToken", tokenDto.type());
    }
}
