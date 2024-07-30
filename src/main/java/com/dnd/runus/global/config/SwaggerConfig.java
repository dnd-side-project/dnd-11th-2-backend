package com.dnd.runus.global.config;

import com.dnd.runus.global.constant.AuthConstant;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.dnd.runus.global.constant.TimeConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.config.Elements.JWT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    private final BuildProperties buildProperties;
    private final Environment environment;

    @Bean
    OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name(JWT)
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme(AuthConstant.TOKEN_TYPE.trim())
                .bearerFormat(JWT)
                .name(AUTHORIZATION)
                .description("JWT 토큰을 입력해주세요 (" + AuthConstant.TOKEN_TYPE + "제외)");

        return new OpenAPI()
                .info(info())
                .addSecurityItem(new SecurityRequirement().addList(JWT))
                .components(new Components().addSecuritySchemes(JWT, securityScheme));
    }

    @Bean
    ModelResolver modelResolver(ObjectMapper objectMapper) { // Load formats from ObjectMapper
        return new ModelResolver(objectMapper);
    }

    @Bean
    OpenApiCustomizer timeFormatCustomizer() {
        return openApi -> Optional.ofNullable(openApi.getComponents())
                .map(Components::getSchemas)
                .ifPresent(schemas -> schemas.forEach((name, schema) -> {
                    @SuppressWarnings("unchecked") // schema.getProperties() returns Map<String, Schema>
                    Map<String, Schema<?>> properties = Optional.ofNullable(schema.getProperties())
                            .map(props -> (Map<String, Schema<?>>) props)
                            .orElse(Collections.emptyMap());
                    properties.forEach((propertyName, propertySchema) -> {
                        if ("date-time".equals(propertySchema.getFormat())) {
                            updatePropertySchema(properties, propertyName, DATE_TIME_FORMAT_EXAMPLE);
                        } else if ("date".equals(propertySchema.getFormat())) {
                            updatePropertySchema(properties, propertyName, DATE_FORMAT_EXAMPLE);
                        } else if ("#/components/schemas/LocalTime".equals(propertySchema.get$ref())) {
                            updatePropertySchema(properties, propertyName, TIME_FORMAT_EXAMPLE);
                        }
                    });
                }));
    }

    @Bean
    OperationCustomizer successResponseBodyWrapper() {
        return (operation, handlerMethod) -> {
            if (!operation.getResponses().containsKey("200")) {
                return operation;
            }
            Content content = operation.getResponses().get("200").getContent();
            if (content == null) {
                return operation;
            }
            content.forEach((mediaTypeKey, mediaType) -> {
                Schema<?> originalSchema = mediaType.getSchema();
                Schema<?> wrappedSchema = new Schema<>();
                wrappedSchema.addProperty("success", new Schema<>().type("bool").example(true));
                wrappedSchema.addProperty("data", originalSchema);
                mediaType.setSchema(wrappedSchema);
            });
            return operation;
        };
    }

    @Bean
    OperationCustomizer apiErrorTypeCustomizer() {
        return (operation, handlerMethod) -> {
            ApiErrorType apiErrorType = handlerMethod.getMethodAnnotation(ApiErrorType.class);
            if (apiErrorType == null) {
                return operation;
            }

            Stream.of(apiErrorType.value())
                    .sorted(Comparator.comparingInt(t -> t.httpStatus().value()))
                    .forEach(type -> {
                        Content content = new Content().addMediaType(APPLICATION_JSON_VALUE, errorMediaType(type));
                        operation
                                .getResponses()
                                .put(
                                        type.httpStatus().value() + " " + type.name(),
                                        new ApiResponse()
                                                .description(type.message())
                                                .content(content));
                    });
            return operation;
        };
    }

    private Info info() {
        String activeProfile = ObjectUtils.isEmpty(environment.getActiveProfiles())
                ? environment.getDefaultProfiles()[0]
                : environment.getActiveProfiles()[0];

        return new Info()
                .title("RunUs API (" + activeProfile + ")")
                .description("API 명세 문서" + "<br> 빌드 일자: " + formatTime(buildProperties.getTime()) + "<br> 실행 일자: "
                        + formatTime((Instant.now())))
                .version(buildProperties.getVersion());
    }

    private String formatTime(Instant instant) {
        return instant.atZone(SERVER_TIMEZONE_ID).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    private MediaType errorMediaType(ErrorType type) {
        Schema<?> errorSchema = new Schema<>();
        errorSchema.properties(Map.of(
                "statusCode",
                new Schema<>().type("int").example(type.httpStatus().value()),
                "code",
                new Schema<>().type("string").example(type.code()),
                "message",
                new Schema<>().type("string").example(type.message())));
        return new MediaType().schema(errorSchema);
    }

    private void updatePropertySchema(Map<String, Schema<?>> properties, String propertyName, String example) {
        Schema<?> propertySchema = properties.get(propertyName);
        properties.replace(
                propertyName,
                new StringSchema()
                        .type(propertySchema.getType())
                        .format(propertySchema.getFormat())
                        .example(example)
                        .description(propertySchema.getDescription()));
    }
}
