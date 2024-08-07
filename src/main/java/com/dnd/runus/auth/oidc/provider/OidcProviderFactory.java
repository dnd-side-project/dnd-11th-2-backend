package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class OidcProviderFactory {

    private final Map<SocialType, OidcProvider> authProviderMap;

    public OidcProviderFactory(List<OidcProvider> providers) {
        authProviderMap = new EnumMap<>(SocialType.class);
        providers.forEach(provider -> authProviderMap.put(provider.getSocialType(), provider));
    }

    public OidcProvider getOidcProviderBy(SocialType socialType) {
        OidcProvider oidcProvider = authProviderMap.get(socialType);

        if (isNull(oidcProvider)) {
            throw new BusinessException(ErrorType.UNSUPPORTED_SOCIAL_TYPE, socialType.getValue());
        }

        return oidcProvider;
    }
}
