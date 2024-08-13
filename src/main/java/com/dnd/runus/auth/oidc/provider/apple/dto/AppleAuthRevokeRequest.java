package com.dnd.runus.auth.oidc.provider.apple.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleAuthRevokeRequest(
        String clientId,
        String clientSecret,
        String token,
        String tokenTypeHint
) {
    public MultiValueMap<String, String> toMultiValueMap() {
        return new LinkedMultiValueMap<>() {{
            add("client_id", clientId);
            add("client_secret", clientSecret);
            add("token", token);
            add("token_type_hint", tokenTypeHint);
        }};
    }
}
