package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public interface OidcProvider {

    SocialType getSocialType();

    Claims getClaimsBy(String idToken);

    default Map<String, String> parseHeaders(String token) {
        String header = token.split("\\.")[0];

        try {
            return new ObjectMapper().readValue(Base64.getUrlDecoder().decode(header), new TypeReference<>() {});
        } catch (IOException e) {
            throw new BusinessException(ErrorType.FAILED_PARSING, e.getMessage());
        }
    }
}
