package com.dnd.runus.infrastructure.weather;

import com.dnd.runus.global.constant.WeatherType;

public record WeatherInfo(
        WeatherType weatherType,
        double apparentTemperature, // 체감온도 (°C)
        double minTemperature, // 최저기온 (°C)
        double maxTemperature, // 최고기온 (°C)
        double humidity,
        double windSpeed // 풍속 (m/s)
        ) {}
