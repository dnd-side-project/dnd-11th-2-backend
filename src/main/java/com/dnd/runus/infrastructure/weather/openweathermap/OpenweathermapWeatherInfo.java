package com.dnd.runus.infrastructure.weather.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenweathermapWeatherInfo(Weather[] weather, Main main, Wind wind) {
    record Weather(int id, String main, String description, String icon) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Main(double temp, double feelsLike, double tempMin, double tempMax, double pressure, double humidity) {}

    record Wind(double speed, double deg, double gust) {}
}
