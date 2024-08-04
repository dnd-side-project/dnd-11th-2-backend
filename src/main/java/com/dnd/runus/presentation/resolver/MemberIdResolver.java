package com.dnd.runus.presentation.resolver;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.userdetails.AuthUserDetails;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class MemberIdResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberId.class)
                && (long.class == parameter.getParameterType() || Long.class == parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            @Nonnull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @Nonnull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        try {
            HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            assert httpServletRequest != null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails user) {
                return user.getId();
            }
        } catch (NullPointerException ex) {
            throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "인증 토큰이 필요합니다");
        }
        log.error("Authentication is null or principal is not instance of AuthUserDetails");
        throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "인증 토큰이 필요합니다");
    }
}
