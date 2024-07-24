package com.dnd.runus.presentation.config;

import com.dnd.runus.presentation.handler.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityWebConfig {
    private final UnauthorizedHandler unauthorizedHandler;
    private final SecurityHttpConfigurer securityHttpConfigurer;

    @Value("${app.api.allow-origins}")
    private List<String> corsOrigins;

    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**",
    };

    private static final String[] OPEN_API_ENDPOINTS = {
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
    };

    private static final String[] READ_ONLY_ENDPOINTS = {
        "/api/v1/examples/**", // TODO: Remove test endpoints
    };

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        AntPathRequestMatcher[] readOnlyEndpoints = Stream.of(READ_ONLY_ENDPOINTS)
                .map(path -> new AntPathRequestMatcher(path, HttpMethod.GET.name()))
                .toArray(AntPathRequestMatcher[]::new);

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
                    auth.requestMatchers(OPEN_API_ENDPOINTS).permitAll();
                    auth.requestMatchers(readOnlyEndpoints).permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(auth -> auth.authenticationEntryPoint(unauthorizedHandler));

        httpSecurity.apply(securityHttpConfigurer);

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L); // Cache preflight
        configuration.setExposedHeaders(List.of(SET_COOKIE, AUTHORIZATION));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
