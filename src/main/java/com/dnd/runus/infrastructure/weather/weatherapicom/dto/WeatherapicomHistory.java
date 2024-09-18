package com.dnd.runus.infrastructure.weather.weatherapicom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherapicomHistory(Location location, Forecast forecast) {
    public record Location(String name, String region, String country) {}

    public record Forecast(List<ForecaseDay> forecastday) {
        public record ForecaseDay(String date, DayInfo day) {
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public record DayInfo(
                    double maxtempC,
                    double maxtempF,
                    double mintempC,
                    double mintempF,
                    double totalsnowCm,
                    int dailyWillItRain,
                    int dailyChanceOfRain,
                    int dailyWillItSnow,
                    int dailyChanceOfSnow) {}
        }
    }
}
