package com.dnd.runus.client.web;

import com.dnd.runus.client.vo.AppleAuthTokenResponse;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
@HttpExchange
public interface AppleAuthClientComponent {

    @GetExchange("/keys")
    OidcPublicKeyList getPublicKeys();

    @PostExchange(
            url = "/token",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE)
    AppleAuthTokenResponse getAccessAppleToken(@RequestBody MultiValueMap<String, String> request);

    @PostExchange(
            url = "/revoke",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE)
    void revoke(@RequestBody MultiValueMap<String, String> request);
}
