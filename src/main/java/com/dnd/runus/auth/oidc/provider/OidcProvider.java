package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Map;

import static com.dnd.runus.global.util.DecodeUtils.decodeBase64;

public interface OidcProvider {

    Claims getClaimsBy(String idToken);

    default Map<String, String> parseHeaders(String token) {
        String header = token.split("\\.")[0];

        try {
            return new ObjectMapper().readValue(decodeBase64(header), Map.class);
        } catch (IOException e) {
            throw new BusinessException(ErrorType.FAILED_PARSING, e.getMessage());
        }
    }
}
