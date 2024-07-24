package com.dnd.runus.auth.token.dto;

import java.time.Instant;

public record AuthTokenClaimDto(
        String subject,
        Instant expireAt
) {
}
