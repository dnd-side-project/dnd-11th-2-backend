package com.dnd.runus.auth.token;

import com.dnd.runus.auth.token.access.AccessTokenProvider;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.global.constant.AuthConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProviderModule {
    private final AccessTokenProvider accessTokenProvider;
    // TODO: RefreshTokenProvider refreshTokenProvider;

    public AuthTokenDto generate(String subject) {
        String accessToken = accessTokenProvider.issueToken(subject);

        log.info("Login success, sub: {}", subject);
        return new AuthTokenDto(accessToken, AuthConstant.TOKEN_TYPE);
    }
}
