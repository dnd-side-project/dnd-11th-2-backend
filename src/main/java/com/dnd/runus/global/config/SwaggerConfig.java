package com.dnd.runus.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    private final BuildProperties buildProperties;
    private final Environment environment;

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(info()).addSecurityItem(new SecurityRequirement().addList("JWT"));
    }

    @Bean
    ModelResolver modelResolver(ObjectMapper objectMapper) { // Load formats from ObjectMapper
        return new ModelResolver(objectMapper);
    }

    @Bean
    OpenApiCustomizer projectPageableCustomizer() {
        return openApi -> customizeOperations(openApi, "pageable", (operation, parameter) -> {
            operation.getParameters().remove(parameter);
            pageableParameter().forEach(operation::addParametersItem);
        });
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

    private void customizeOperations(OpenAPI openApi, String paramName, BiConsumer<Operation, Parameter> customizer) {
        openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> !ObjectUtils.isEmpty(operation.getParameters()))
                .forEach(operation -> operation.getParameters().stream()
                        .filter(parameter -> paramName.equals(parameter.getName()))
                        .findFirst()
                        .ifPresent(parameter -> customizer.accept(operation, parameter)));
    }

    private List<Parameter> pageableParameter() {
        Schema<?> pageSchema = new StringSchema().example("0").description("페이지 번호 (0부터 시작)");
        Schema<?> sizeSchema = new StringSchema().example("20").description("한 페이지에 보여줄 항목 수");
        Schema<?> sortSchema = new StringSchema().example("id,asc").description("정렬 조건 (ex. id,asc or id,desc)");

        return List.of(
                new QueryParameter().name("page").schema(pageSchema),
                new QueryParameter().name("size").schema(sizeSchema),
                new QueryParameter().name("sort").schema(sortSchema));
    }

    private String formatTime(Instant instant) {
        return instant.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
