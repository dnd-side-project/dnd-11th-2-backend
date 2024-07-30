package com.dnd.runus.presentation.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.time.*;

import static com.dnd.runus.global.constant.TimeConstant.*;
import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class ObjectMapperConfig {
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        DateTimeFormatter dateFormatter = ofPattern(DATE_FORMAT);
        DateTimeFormatter dateTimeFormatter = ofPattern(DATE_TIME_FORMAT);

        Module dateTimeModule = new SimpleModule()
                .addSerializer(LocalTime.class, new LocalTimeSerializer(ofPattern(TIME_FORMAT)))
                .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
                .addSerializer(
                        OffsetDateTime.class,
                        new OffsetDateTimeSerializer(
                                OffsetDateTimeSerializer.INSTANCE, false, false, dateTimeFormatter) {})
                .addSerializer(
                        Instant.class,
                        new InstantSerializer(
                                InstantSerializer.INSTANCE,
                                false,
                                false,
                                dateTimeFormatter.withZone(SERVER_TIMEZONE_ID)) {});

        objectMapper.registerModule(dateTimeModule);

        return objectMapper;
    }
}
