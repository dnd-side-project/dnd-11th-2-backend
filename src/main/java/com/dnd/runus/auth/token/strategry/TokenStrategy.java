package com.dnd.runus.auth.token.strategry;

import com.dnd.runus.auth.token.dto.AuthTokenClaimDto;

public interface TokenStrategy {
    /**
     * 새로운 토큰을 발행
     *
     * @param subject - 토큰 구분 대상
     * @return - 발급한 토큰
     */
    String generateToken(String subject);

    AuthTokenClaimDto getClaims(String token);
}
