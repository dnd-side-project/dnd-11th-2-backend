package com.dnd.runus.infrastructure.weather.weatherapicom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherapicomCurrent(Current current) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Current(
            double tempC,
            double tempF,
            int isDay,
            Condition condition,
            double windMph,
            int humidity,
            int cloud,
            double feelslikeC) {
        public record Condition(int code) {}
    }
}