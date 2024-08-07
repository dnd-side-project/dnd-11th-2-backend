package com.dnd.runus.client.vo;


import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
public record AppleAuthRevokeRequest(
    String client_id,
    String client_secret,
    String token,
    String token_type_hint
) {
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("token", token);
        params.add("token_type_hint", token_type_hint);

        return params;
    }
}
