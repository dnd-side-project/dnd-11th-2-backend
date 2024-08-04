package com.dnd.runus.auth.oidc.provider.impl;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.PublicKeyProvider;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import com.dnd.runus.client.web.AppleAuthClient;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@RequiredArgsConstructor
public class AppleAuthProvider implements OidcProvider {

    private final AppleAuthClient appleAuthClient;
    private final PublicKeyProvider publicKeyProvider;

    @Override
    public Claims getClaimsBy(String identityToken) {
        // 퍼블릭 키 리스트
        OidcPublicKeyList publicKeys = appleAuthClient.getPublicKeys();
        // 토큰 헤더에서 디코딩 -> 퍼블릭 키 리스트 대조회 n,e갑 디코딩 후 퍼블릭 키 생성
        PublicKey publicKey = publicKeyProvider.generatePublicKey(parseHeaders(identityToken), publicKeys);

        return parseClaims(identityToken, publicKey);
    }

    public Claims parseClaims(String token, PublicKey publicKey) {
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
            throw new AuthException(ErrorType.INVALID_ACCESS_TOKEN, e.getMessage());
        }
    }
}
