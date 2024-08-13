package com.dnd.runus.auth.oidc.provider.apple.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleAuthTokenRequest(
        String code,
        String clientId,
        String clientSecret,
        String grantType
) {
    public MultiValueMap<String, String> toMultiValueMap() {
        return new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", clientId);
            add("client_secret", clientSecret);
            add("grant_type", grantType);
        }};
    }
}
