package com.dnd.runus.client.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleAuthTokenResponse(
    String accessToken,
    String expiresIn,
    String idToken,
    String refreshToken,
    String tokenType,
    String error
) {

}
