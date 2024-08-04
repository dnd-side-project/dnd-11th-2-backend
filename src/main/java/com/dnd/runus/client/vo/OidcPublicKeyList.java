package com.dnd.runus.client.vo;

import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;

import java.util.List;

public record OidcPublicKeyList(List<OidcPublicKey> keys) {

    public OidcPublicKey getMatchedKeyBy(String kid, String alg) {
        // kid, alg 일치한 퍼블릭 키
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorType.MALFORMED_ACCESS_TOKEN, "검증할 수 없는 토큰입니다."));
    }
}
