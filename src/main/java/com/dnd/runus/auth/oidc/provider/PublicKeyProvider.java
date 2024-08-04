package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.client.vo.OidcPublicKey;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import static com.dnd.runus.global.util.DecodeUtils.decodeBase64;

@Component
public class PublicKeyProvider {

    public PublicKey generatePublicKey(final Map<String, String> tokenHeaders, final OidcPublicKeyList publicKeys) {

        OidcPublicKey publicKey = publicKeys.getMatchedKeyBy(tokenHeaders.get("kid"), tokenHeaders.get("alg"));

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(final OidcPublicKey key) {

        try {
            final byte[] nByte = decodeBase64(key.n());
            final byte[] eByte = decodeBase64(key.e());

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
