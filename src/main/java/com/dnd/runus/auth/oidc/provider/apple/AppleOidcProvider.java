package com.dnd.runus.auth.oidc.provider.apple;

import com.dnd.runus.auth.oidc.client.AppleAuthClient;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.apple.dto.AppleAuthRevokeRequest;
import com.dnd.runus.auth.oidc.provider.apple.dto.AppleAuthTokenRequest;
import com.dnd.runus.auth.oidc.vo.OidcPublicKeys;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;

import static com.dnd.runus.auth.oidc.provider.JwtParser.parseClaims;
import static com.dnd.runus.auth.oidc.provider.JwtParser.parseHeaders;
import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOidcProvider implements OidcProvider {

    private final AppleAuthClient appleAuthClient;
    private final ObjectMapper objectMapper;

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
        OidcPublicKeys publicKeys = appleAuthClient.getPublicKeys();
        PublicKey publicKey = publicKeys.generatePublicKey(parseHeaders(objectMapper, identityToken));

        Claims claims = parseClaims(identityToken, publicKey);
        verifyClaims(claims);
        return claims;
    }

    @Override
    public String getAccessToken(String code) {
        try {
            AppleAuthTokenRequest request = AppleAuthTokenRequest.builder()
                    .clientId(clientId)
                    .clientSecret(createClientSecret())
                    .code(code)
                    .grantType("authorization_code")
                    .build();
            return appleAuthClient.getAuthToken(request.toMultiValueMap()).accessToken();
        } catch (HttpClientErrorException e) {
            throw new BusinessException(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
        }
    }

    @Override
    public void revoke(String accessToken) {
        try {
            AppleAuthRevokeRequest request = AppleAuthRevokeRequest.builder()
                    .clientId(clientId)
                    .clientSecret(createClientSecret())
                    .token(accessToken)
                    .tokenTypeHint("access_token")
                    .build();
            appleAuthClient.revoke(request.toMultiValueMap());
        } catch (HttpClientErrorException e) {
            log.warn("failed token revoke :{}", e.getMessage());
            throw new BusinessException(ErrorType.FAILED_AUTHENTICATION, e.getMessage());
        }
    }

    private void verifyClaims(Claims claims) {

        // FIXME 클라이언트에서 검증 관련해서 논의하고 삭제 또는 주석 삭제
        //        if (claims.get("nonce") == null) {
        //            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify the nonce");
        //        }
        //        if (!"https://appleid.apple.com".equals(claims.getIssuer())) {
        //            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify iss");
        //        }
        //        if (claims.getAudience() == null || !claims.getAudience().contains(clientId)) {
        //            throw new AuthException(ErrorType.TAMPERED_ACCESS_TOKEN, "Can't verify audience");
        //        }
        //
        //        if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
        //            throw new AuthException(ErrorType.EXPIRED_ACCESS_TOKEN, "Can't verify exp");
        //        }
    }

    private String createClientSecret() {
        Date expiration = Date.from(Instant.now().plus(10, MINUTES));

        return Jwts.builder()
                .header()
                .add("kid", keyId)
                .add("alg", SIG.ES256.getId())
                .and()
                .issuer(teamId)
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiration)
                .audience()
                .add("https://appleid.apple.com")
                .and()
                .subject(clientId)
                .signWith(KeyHolder.INSTANCE, SIG.ES256)
                .compact();
    }

    private static class KeyHolder {
        private static final PrivateKey INSTANCE = getPrivateKey();

        private static PrivateKey getPrivateKey() {
            ClassPathResource resource = new ClassPathResource("app/apple/AuthKey.p8");
            try {
                Reader pemReader = new InputStreamReader(resource.getInputStream());
                PEMParser pemParser = new PEMParser(pemReader);
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
                return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
            } catch (IOException e) {
                throw new BusinessException(ErrorType.UNHANDLED_EXCEPTION, e.getMessage());
            }
        }
    }
}
