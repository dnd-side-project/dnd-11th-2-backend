package com.dnd.runus.infrastructure.weather.openweathermap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenweathermapCurrent(Weather[] weather, Main main, Wind wind, long dt, Sys sys) {
    public record Weather(int id, String main, String description, String icon) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Main(
            double temp, double feelsLike, double tempMin, double tempMax, double pressure, double humidity) {}

    public record Wind(double speed, double deg, double gust) {}

    public record Sys(long sunrise, long sunset) {}

    public OpenweathermapCurrent {
        if (weather == null || weather.length == 0) {
            throw new IllegalStateException("날씨 정보를 가져올 수 없습니다.");
        }
    }

    public boolean isDay() {
        return dt > sys.sunrise && dt < sys.sunset;
    }
}
