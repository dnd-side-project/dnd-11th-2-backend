package com.dnd.runus.infrastructure.weather.mock;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import org.springframework.stereotype.Component;

@Component
public class MockWeatherClient implements WeatherClient {
    @Override
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        return new WeatherInfo(WeatherType.CLEAR, 20.0, 15.0, 25.0, 0.5, 1.0);
    }
}
