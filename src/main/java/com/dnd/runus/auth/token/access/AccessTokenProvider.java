package com.dnd.runus.auth.token.access;

import com.dnd.runus.auth.token.dto.AuthTokenClaimDto;
import com.dnd.runus.auth.token.strategry.TokenStrategy;
import com.dnd.runus.global.constant.AuthConstant;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {
    private final TokenStrategy strategy;

    public AccessTokenProvider(@Qualifier("accessTokenStrategy") TokenStrategy tokenStrategy) {
        this.strategy = tokenStrategy;
    }

    public String resolveToken(String headerAuth) {
        if (StringUtils.isNotBlank(headerAuth) && headerAuth.startsWith(AuthConstant.TOKEN_TYPE)) {
            return headerAuth.substring(AuthConstant.TOKEN_TYPE.length()).trim();
        }
        return "";
    }

    public String issueToken(String subject) {
        return strategy.generateToken(subject);
    }

    public AuthTokenClaimDto getClaims(String token) {
        return strategy.getClaims(token);
    }
}
