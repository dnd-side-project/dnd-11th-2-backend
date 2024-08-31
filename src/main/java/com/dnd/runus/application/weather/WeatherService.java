package com.dnd.runus.application.weather;

import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.presentation.v1.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;

    public WeatherResponse getWeather(double longitude, double latitude) {
        WeatherInfo info = weatherClient.getWeatherInfo(longitude, latitude);
        return new WeatherResponse(
                info.weatherType().getKoreanName(),
                info.weatherType().getKoreanDescription(),
                info.weatherType().getIconUrl(),
                (int) info.apparentTemperature(),
                (int) info.minTemperature(),
                (int) info.maxTemperature());
    }
}
