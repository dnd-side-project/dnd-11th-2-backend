package com.dnd.runus.presentation.config;

import com.dnd.runus.presentation.filter.AuthenticationCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityFilterConfig {
    @Bean
    AuthenticationCheckFilter authenticationCheckFilter() {
        return new AuthenticationCheckFilter();
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
