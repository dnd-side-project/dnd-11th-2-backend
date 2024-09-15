package com.dnd.runus.auth.token.dto;

public record AuthTokenDto(
        String accessToken,
        String refreshToken,
        String type
) {
}
