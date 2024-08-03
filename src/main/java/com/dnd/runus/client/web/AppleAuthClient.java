package com.dnd.runus.client.web;

import com.dnd.runus.client.vo.ApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleAuthClient {

    private final AppleAuthClientComponent appleAuthClientComponent;

    public ApplePublicKeyResponse getPublicKeys() {
        return appleAuthClientComponent.getPublicKeys();
    }
}
