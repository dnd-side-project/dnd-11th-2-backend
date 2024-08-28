package com.dnd.runus.infrastructure.weather;

public interface WeatherClient {
    WeatherInfo getWeatherInfo(double longitude, double latitude);
}
