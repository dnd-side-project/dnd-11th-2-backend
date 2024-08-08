package com.dnd.runus.auth.oidc.provider.impl;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.PublicKeyProvider;
import com.dnd.runus.client.vo.AppleAuthRevokeRequest;
import com.dnd.runus.client.vo.AppleAuthTokenRequest;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import com.dnd.runus.client.web.AppleAuthClient;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleAuthProvider implements OidcProvider {

    private final AppleAuthClient appleAuthClient;
    private final PublicKeyProvider publicKeyProvider;

    @Value("${oauth.apple.key_id}")
    private String keyId;

    @Value("${oauth.apple.team_id}")
    private String teamId;

    @Value("${oauth.apple.client_id}")
    private String clientId;

    @Override
    public SocialType getSocialType() {
        return SocialType.APPLE;
    }

    @Override
    public Claims getClaimsBy(String identityToken) {
        OidcPublicKeyList publicKeys = appleAuthClient.getPublicKeys();
        PublicKey publicKey = publicKeyProvider.generatePublicKey(parseHeaders(identityToken), publicKeys);

        Claims claims = parseClaims(identityToken, publicKey);
        verifyClaims(claims);
        return claims;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            return appleAuthClient
                    .getAccessAppleToken(AppleAuthTokenRequest.builder()
                            .client_secret(createClientSecret())
                            .client_id(clientId)
                            .code(code)
                            .grant_type("authorization_code")
                            .build())
                    .accessToken();
        } catch (HttpClientErrorException e) {
            throw new BusinessException(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
        }
    }

    @Override
    public void revoke(String accessToken) {
        try {
            appleAuthClient.revokeAccount(AppleAuthRevokeRequest.builder()
                    .client_id(clientId)
                    .client_secret(createClientSecret())
                    .token(accessToken)
                    .token_type_hint("access_token")
                    .build());
        } catch (HttpClientErrorException e) {
            log.warn("failed token revoke :{}", e.getMessage());
            throw new BusinessException(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
        }
    }

    private Claims parseClaims(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | MalformedJwtException e) {
            // 토큰 서명 검증 또는 구조 문제
            throw new AuthException(ErrorType.MALFORMED_ACCESS_TOKEN, e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorType.EXPIRED_ACCESS_TOKEN, e.getMessage());
        }
    }

    private void verifyClaims(Claims claims) {

        // FIXME 클라이언트에서 nonce 추가 확인 되면 주석 삭제
        //        if (claims.get("nonce") == null) {
        //            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify the nonce");
        //        }
        if (!"https://appleid.apple.com".equals(claims.getIssuer())) {
            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify iss");
        }
        if (claims.getAudience() == null || !claims.getAudience().contains(clientId)) {
            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify audience");
        }

        Instant exp = Instant.ofEpochMilli(claims.getExpiration().getTime());
        if (Instant.now().isAfter(exp)) {
            throw new AuthException(ErrorType.EXPIRED_ACCESS_TOKEN, "Can't verify exp");
        }
    }

    private PrivateKey getPrivateKey() {
        ClassPathResource resource = new ClassPathResource("AuthKey_" + keyId + ".p8");
        try {
            String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            Reader pemReader = new StringReader(privateKey);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

            converter.getPrivateKey(object);
            return converter.getPrivateKey(object);
        } catch (IOException e) {
            throw new BusinessException(ErrorType.UNHANDLED_EXCEPTION, e.getMessage());
        }
    }

    private String createClientSecret() {
        Date expirationDate = Date.from(LocalDateTime.now()
                .plusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        return Jwts.builder()
                .header()
                .add("kid", keyId)
                .add("alg", "ES256")
                .and()
                .issuer(teamId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .audience()
                .add("https://appleid.apple.com")
                .and()
                .subject(clientId)
                .signWith(getPrivateKey(), SIG.ES256)
                .compact();
    }
}
