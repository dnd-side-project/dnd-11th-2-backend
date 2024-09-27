package com.dnd.runus.infrastructure.weather.weatherapicom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

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

    public WeatherapicomHistory {
        if (location == null
                || forecast == null
                || isEmpty(forecast.forecastday())
                || forecast.forecastday().getFirst() == null) {
            throw new IllegalStateException("과거 날씨 정보를 가져올 수 없습니다.");
        }
    }
}
