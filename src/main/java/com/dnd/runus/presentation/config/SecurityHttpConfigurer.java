package com.dnd.runus.presentation.config;

import com.dnd.runus.presentation.filter.AuthenticationCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityHttpConfigurer implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
    private final AuthenticationCheckFilter authenticationCheckFilter;

    @Override
    public void init(HttpSecurity httpSecurity) {}

    @Override
    public void configure(HttpSecurity httpSecurity) {
        httpSecurity.addFilterBefore(authenticationCheckFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
