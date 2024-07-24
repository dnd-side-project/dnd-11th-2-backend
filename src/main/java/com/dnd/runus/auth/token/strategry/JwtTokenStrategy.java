package com.dnd.runus.auth.token.strategry;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.token.dto.AuthTokenClaimDto;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

import static com.dnd.runus.global.exception.type.ErrorType.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class JwtTokenStrategy implements TokenStrategy {
    private final Duration expiration;
    private final SecretKey secretKey;
    private final SecureDigestAlgorithm<? super SecretKey, ?> algorithm;

    private static final Map<Class<? extends Exception>, ErrorType> ERROR_TYPES = Map.of(
            ExpiredJwtException.class, EXPIRED_ACCESS_TOKEN,
            MalformedJwtException.class, MALFORMED_ACCESS_TOKEN,
            SignatureException.class, TAMPERED_ACCESS_TOKEN,
            UnsupportedJwtException.class, UNSUPPORTED_JWT_TOKEN);

    public static JwtTokenStrategy of(String rawSecretKey, Duration expiration, MacAlgorithm algorithm) {
        byte[] keyBytes = Base64.getEncoder().encode(rawSecretKey.getBytes()); // 비공개 키를 Base64 인코딩
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return new JwtTokenStrategy(expiration, secretKey, algorithm);
    }

    public String generateToken(String subject) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(expiration);

        return Jwts.builder()
                .header()
                .add(createHeader())
                .and()
                .issuer("runus")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .claims(createClaims(subject, expireAt))
                .signWith(secretKey, algorithm)
                .compact();
    }

    public AuthTokenClaimDto getClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new AuthTokenClaimDto(
                    claims.getSubject(), claims.getExpiration().toInstant());
        } catch (JwtException jwtException) {
            ErrorType type = ERROR_TYPES.getOrDefault(jwtException.getClass(), FAILED_AUTHENTICATION);
            throw new AuthException(type, jwtException.getMessage());
        }
    }

    private static Map<String, Object> createHeader() {
        return Map.of(Header.TYPE, Header.JWT_TYPE);
    }

    private static Map<String, Object> createClaims(String subject, Instant expireAt) {
        return Map.of(Claims.SUBJECT, subject, Claims.EXPIRATION, Date.from(expireAt));
    }

    private static class Header {
        static final String TYPE = "typ";
        static final String JWT_TYPE = "JWT";
    }
}
