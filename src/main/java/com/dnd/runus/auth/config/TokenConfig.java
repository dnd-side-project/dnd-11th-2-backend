package com.dnd.runus.auth.config;

import com.dnd.runus.auth.token.strategry.JwtTokenStrategy;
import com.dnd.runus.auth.token.strategry.TokenStrategy;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
class TokenConfig {
    @Bean("accessTokenStrategy")
    TokenStrategy accessTokenStrategy(
            @Value("${app.auth.token.access.expiration}") Duration accessExpiration,
            @Value("${app.auth.token.access.secret-key}") String secretKey) {
        return JwtTokenStrategy.of(secretKey, accessExpiration, Jwts.SIG.HS256);
    }

    @Bean("refreshTokenStrategy")
    TokenStrategy refreshTokenStrategy(
            @Value("${app.auth.token.refresh.expiration}") Duration refreshExpiration,
            @Value("${app.auth.token.refresh.secret-key}") String secretKey) {
        return JwtTokenStrategy.of(secretKey, refreshExpiration, Jwts.SIG.HS256);
    }
}
