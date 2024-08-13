package com.dnd.runus.auth.oidc.provider;

import com.dnd.runus.global.constant.SocialType;
import io.jsonwebtoken.Claims;

public interface OidcProvider {

    SocialType getSocialType();

    Claims getClaimsBy(String idToken);

    String getAccessToken(String code);

    /**
     * 소셜로그인 연결 해제
     *
     * @param accessToken 엑세스토큰
     */
    void revoke(String accessToken);
}
