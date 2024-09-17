package com.dnd.runus.presentation.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.*;

import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_HOUR;
import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_MINUTE;
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
                                dateTimeFormatter.withZone(SERVER_TIMEZONE_ID)) {})
                .addSerializer(Duration.class, new CustomDurationSerializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
                .addDeserializer(Duration.class, new CustomDurationDeserializer());
        objectMapper.registerModule(dateTimeModule);
        return objectMapper;
    }

    public static class CustomDurationSerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider)
                throws IOException {
            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / SECONDS_PER_HOUR;
            long minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
            long seconds = totalSeconds % SECONDS_PER_MINUTE;
            // HH:mm:ss or HHH:mm:ss
            String formattedDuration =
                    String.format("%s:%02d:%02d", (hours < 10 ? "0" + hours : hours), minutes, seconds);
            generator.writeString(formattedDuration);
        }
    }

    public static class CustomDurationDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String[] parts = parser.getText().split(":");
            if (parts.length != 3) {
                throw new JsonMappingException(parser, "Invalid duration format, expected HH:mm:ss");
            }
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
    }
}
