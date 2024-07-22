package com.dnd.runus.presentation.handler;

import com.dnd.runus.presentation.dto.response.ApiErrorDto;
import com.dnd.runus.presentation.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "com.dnd.runus")
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType,
                                  @Nonnull MediaType selectedContentType, @Nonnull Class selectedConverterType,
                                  @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        HttpStatus resolve = HttpStatus.resolve(servletResponse.getStatus());
        if (resolve == null || !resolve.is2xxSuccessful()) {
            if (body instanceof ApiErrorDto error) {
                return ApiResponse.fail(error);
            }
            log.error("Unreachable response handling! request: {}, response: {}, body: {}", request, response, body);
            throw new UnsupportedOperationException("Unreachable response handling!" + body);
        }

        if (body instanceof String) {
            // String 타입을 Wrapper로 감싸면 StringConverter에서 처리할 수 없음
            // 따라서 ObjectMapper를 통해 직렬화하여 반환
            ApiResponse<?> res = ApiResponse.success(body);
            try {
                String stringRes = objectMapper.writeValueAsString(res);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return stringRes;
            } catch (JsonProcessingException err){
                throw new RuntimeException("Failed to convert BaseResponse to JSON");
            }
        }
        return ApiResponse.success(body);
    }
}
