package com.dnd.runus.infrastructure.weather.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenweathermapWeatherInfo(Weather[] weather, Main main, Wind wind) {
    record Weather(int id, String main, String description, String icon) {}

    record Main(double temp, double feels_like, double temp_min, double temp_max, double pressure, double humidity) {}

    record Wind(double speed, double deg, double gust) {}
}
