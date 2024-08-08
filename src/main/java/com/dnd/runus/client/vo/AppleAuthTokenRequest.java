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
        return new LinkedMultiValueMap<>(){{
            add("code", code);
            add("client_id", client_id);
            add("client_secret", client_secret);
                add("grant_type", grant_type);
        }};
    }
}
