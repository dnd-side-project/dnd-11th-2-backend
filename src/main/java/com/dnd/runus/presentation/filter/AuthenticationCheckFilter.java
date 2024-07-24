package com.dnd.runus.presentation.filter;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.token.access.AccessTokenProvider;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationCheckFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final AccessTokenProvider accessTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            checkAccessToken(request);
        } catch (AuthException authException) {
            log.warn("Failed to authenticate: {}, message: {}", authException.getType(), authException.getMessage());
            authenticationEntryPoint.commence(request, response, authException);
            return;
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void checkAccessToken(HttpServletRequest request) {
        String accessToken = accessTokenProvider.resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (StringUtils.isBlank(accessToken)) {
            return;
        }

        String subject = accessTokenProvider.getClaims(accessToken).subject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
