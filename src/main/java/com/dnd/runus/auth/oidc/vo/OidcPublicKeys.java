package com.dnd.runus.auth.oidc.vo;

import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record OidcPublicKeys(List<OidcPublicKey> keys) {
    public PublicKey generatePublicKey(Map<String, String> tokenHeaders) {

        return findMatchedKey(tokenHeaders.get("kid"), tokenHeaders.get("alg")).generatePublicKey();
    }

    private OidcPublicKey findMatchedKey(String kid, String alg) {
        // kid, alg 일치한 퍼블릭 키
        return keys.stream()
                .filter(key -> Objects.equals(kid, key.kid()) && Objects.equals(alg, key.alg()))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorType.MALFORMED_ACCESS_TOKEN, "검증할 수 없는 토큰입니다."));
    }

    private record OidcPublicKey(String kty, String kid, String use, String alg, String n, String e) {
        private PublicKey generatePublicKey() {
            try {
                byte[] nByte = Base64.getUrlDecoder().decode(n);
                byte[] eByte = Base64.getUrlDecoder().decode(e);

                BigInteger modulus = new BigInteger(1, nByte);
                BigInteger exponent = new BigInteger(1, eByte);

                KeyFactory keyFactory = KeyFactory.getInstance(kty);

                return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new BusinessException(ErrorType.UNSUPPORTED_JWT_TOKEN, e.getMessage());
            }
        }
    }
}
