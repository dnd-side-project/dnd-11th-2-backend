package com.dnd.runus.infrastructure.weather.openweathermap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenweathermapForecast(String cod, List[] list) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record List(long dt, Main main) {

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Main(
                double temp,
                double feelsLike,
                double tempMin,
                double tempMax,
                int pressure,
                int humidity,
                double tempKf) {}
    }

    public OpenweathermapForecast {
        if (list == null || list.length == 0) {
            throw new IllegalStateException("날씨 정보를 가져올 수 없습니다.");
        }
    }

    public double findMinTemp() {
        return Arrays.stream(list)
                .map(List::main)
                .map(List.Main::tempMin)
                .min(Double::compare)
                .orElseThrow(() -> new IllegalStateException("최저 온도를 가져올 수 없습니다."));
    }

    public double findMaxTemp() {
        return Arrays.stream(list)
                .map(List::main)
                .map(List.Main::tempMax)
                .max(Double::compare)
                .orElseThrow(() -> new IllegalStateException("최고 온도를 가져올 수 없습니다."));
    }
}
