package com.dnd.runus.client.web;

import com.dnd.runus.client.vo.OidcPublicKeyList;
import org.springframework.stereotype.Component;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@Component
@HttpExchange
public interface AppleAuthClientComponent {
    @GetExchange("/keys")
    OidcPublicKeyList getPublicKeys();
}
