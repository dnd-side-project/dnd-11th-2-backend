package com.dnd.runus.presentation.handler;

import com.dnd.runus.presentation.dto.response.ApiSuccessResponse;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.dnd.runus")
public class SuccessResponseHandlerAdvise implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType,
                                  @Nonnull MediaType selectedContentType, @Nonnull Class selectedConverterType,
                                  @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        int status = servletResponse.getStatus();
        HttpStatus resolve = HttpStatus.resolve(status);
        if (resolve == null || !resolve.is2xxSuccessful()) {
            return body;
        }

        if (body instanceof String str) {
            // FIXME: body가 String일 경우, ApiSuccessResponse.of()로 감싸면 에러 발생
            return str;
        }
        return ApiSuccessResponse.of(status, body);
    }
}
