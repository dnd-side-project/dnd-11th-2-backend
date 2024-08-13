package com.dnd.runus.auth.oidc.client;

import com.dnd.runus.auth.oidc.provider.apple.dto.AppleAuthTokenResponse;
import com.dnd.runus.auth.oidc.vo.OidcPublicKeys;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface AppleAuthClient {

    @GetExchange("/keys")
    OidcPublicKeys getPublicKeys();

    @PostExchange(url = "/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AppleAuthTokenResponse getAuthToken(@RequestBody MultiValueMap<String, String> request);

    @PostExchange(url = "/revoke", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void revoke(@RequestBody MultiValueMap<String, String> request);
}
