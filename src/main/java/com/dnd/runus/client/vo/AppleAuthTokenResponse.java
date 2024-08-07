package com.dnd.runus.client.vo;


public record AppleAuthTokenResponse(
    String access_token,
    String expires_in,
    String id_token,
    String refresh_token,
    String token_type,
    String error
) {

}
