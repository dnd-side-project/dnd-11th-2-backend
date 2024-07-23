package com.dnd.runus.presentation.handler;

import com.dnd.runus.presentation.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class NullResponseHandler implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, HttpServletResponse response, @Nonnull Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (response.getContentType() == null && response.getStatus() == HttpServletResponse.SC_OK) {
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(null)));
        }
    }
}
