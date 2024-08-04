package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.auth.oidc.provider.impl.AppleAuthProvider;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class OidcProviderFactory {

    private final Map<SocialType, OidcProvider> authProviderMap;
    private final AppleAuthProvider appleAuthProvider;

    public OidcProviderFactory(AppleAuthProvider appleAuthProvider) {
        authProviderMap = new EnumMap<>(SocialType.class);
        this.appleAuthProvider = appleAuthProvider;

        init();
    }

    private void init() {
        authProviderMap.put(SocialType.APPLE, appleAuthProvider);
    }

    public Claims getClaims(SocialType socialType, String idToken) {
        return getProvider(socialType).getClaimsBy(idToken);
    }

    private OidcProvider getProvider(SocialType socialType) {
        OidcProvider oidcProvider = authProviderMap.get(socialType);

        if (isNull(oidcProvider)) {
            throw new BusinessException(ErrorType.UNSUPPORTED_SOCIAL_TYPE, socialType.getValue());
        }

        return oidcProvider;
    }
}
