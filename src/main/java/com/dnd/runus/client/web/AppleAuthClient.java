package com.dnd.runus.client.web;

import com.dnd.runus.client.vo.AppleAuthRevokeRequest;
import com.dnd.runus.client.vo.AppleAuthTokenRequest;
import com.dnd.runus.client.vo.AppleAuthTokenResponse;
import com.dnd.runus.client.vo.OidcPublicKeyList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleAuthClient {

    private final AppleAuthClientComponent appleAuthClientComponent;

    public OidcPublicKeyList getPublicKeys() {
        return appleAuthClientComponent.getPublicKeys();
    }

    public AppleAuthTokenResponse getAccessAppleToken(AppleAuthTokenRequest request) {
        return appleAuthClientComponent.getAccessAppleToken(request.toMultiValueMap());
    }

    public void revokeAccount(AppleAuthRevokeRequest request) {
        appleAuthClientComponent.revoke(request.toMultiValueMap());
    }
}
