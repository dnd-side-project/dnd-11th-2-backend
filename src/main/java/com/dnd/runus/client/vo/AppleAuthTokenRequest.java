package com.dnd.runus.client.vo;

import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
public record AppleAuthTokenRequest(
    String code,
    String client_id,
    String client_secret,
    String grant_type
) {

    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("grant_type", grant_type);

        return params;
    }
}
