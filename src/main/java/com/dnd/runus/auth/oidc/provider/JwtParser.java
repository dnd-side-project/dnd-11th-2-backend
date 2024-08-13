package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

public final class JwtParser {
    JwtParser() {}

    public static Map<String, String> parseHeaders(ObjectMapper objectMapper, String token) {
        String header = token.split("\\.")[0];

        try {
            return objectMapper.readValue(Base64.getUrlDecoder().decode(header), new TypeReference<>() {});
        } catch (IOException e) {
            throw new BusinessException(ErrorType.FAILED_PARSING, e.getMessage());
        }
    }

    public static Claims parseClaims(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | MalformedJwtException e) {
            // 토큰 서명 검증 실패 또는 구조 문제
            throw new AuthException(ErrorType.MALFORMED_ACCESS_TOKEN, e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorType.EXPIRED_ACCESS_TOKEN, e.getMessage());
        }
    }
}
