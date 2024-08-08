package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.client.vo.OidcPublicKey;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

public final class PublicKeyProvider {

    public static PublicKey generatePublicKey(Map<String, String> tokenHeaders, OidcPublicKeyList publicKeys) {

        OidcPublicKey publicKey = publicKeys.getMatchedKeyBy(tokenHeaders.get("kid"), tokenHeaders.get("alg"));

        return getPublicKey(publicKey);
    }

    private static PublicKey getPublicKey(OidcPublicKey key) {

        try {
            byte[] nByte = Base64.getUrlDecoder().decode(key.n());
            byte[] eByte = Base64.getUrlDecoder().decode(key.e());

            BigInteger n = new BigInteger(1, nByte);
            BigInteger e = new BigInteger(1, eByte);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.kty());

            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(ErrorType.UNSUPPORTED_JWT_TOKEN, e.getMessage());
        }
    }
}
