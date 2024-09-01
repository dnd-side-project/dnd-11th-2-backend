package com.dnd.runus.infrastructure.weather.openweathermap;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.dnd.runus.global.exception.type.ErrorType.WEATHER_API_ERROR;

@Component
@RequiredArgsConstructor
public class OpenweathermapWeatherClient implements WeatherClient {
    private final OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient;

    @Value("${weather.openweathermap.api-key}")
    private String apiKey;

    @Override
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        OpenweathermapWeatherInfo info = openweathermapWeatherHttpClient.getWeatherInfo(longitude, latitude, apiKey);

        if (info == null || info.weather() == null || info.weather().length == 0) {
            throw new BusinessException(WEATHER_API_ERROR, "날씨 정보를 가져올 수 없습니다.");
        }

        return new WeatherInfo(
                mapWeatherType(info.weather()[0].id()),
                info.main().feels_like(),
                info.main().temp_min(),
                info.main().temp_max(),
                info.main().humidity(),
                info.wind().speed());
    }

    private WeatherType mapWeatherType(int weatherId) {
        switch (weatherId / 100) {
            case 2:
                return WeatherType.STORM;
            case 3, 5:
                return WeatherType.RAIN;
            case 6:
                return WeatherType.SNOW;
            case 7:
                return WeatherType.FOG;
            case 8:
                if (weatherId == 800) {
                    return WeatherType.CLEAR;
                } else if (weatherId == 801) {
                    return WeatherType.CLOUDY;
                } else {
                    return WeatherType.CLOUDY_MORE;
                }
            default:
                return WeatherType.CLOUDY_MORE;
        }
    }
}
