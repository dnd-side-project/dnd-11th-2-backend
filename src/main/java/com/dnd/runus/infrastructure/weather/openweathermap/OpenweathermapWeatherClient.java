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
        String unit = "metric";
        OpenweathermapWeatherInfo info =
                openweathermapWeatherHttpClient.getWeatherInfo(longitude, latitude, unit, apiKey);

        if (info == null || info.weather() == null || info.weather().length == 0) {
            throw new BusinessException(WEATHER_API_ERROR, "날씨 정보를 가져올 수 없습니다.");
        }

        return new WeatherInfo(
                mapWeatherType(info.weather()[0].id(), info.isDay()),
                info.main().feelsLike(),
                info.main().tempMin(),
                info.main().tempMax(),
                info.main().humidity(),
                info.wind().speed());
    }

    private WeatherType mapWeatherType(int weatherId, boolean isDay) {
        return switch (weatherId / 100) {
            case 2 -> WeatherType.STORM;
            case 3, 5 -> WeatherType.RAIN;
            case 6 -> WeatherType.SNOW;
            case 7 -> WeatherType.FOG;
            case 8 -> {
                if (weatherId == 800) {
                    yield isDay ? WeatherType.CLEAR : WeatherType.CLEAR_NIGHT;
                } else if (weatherId == 801) {
                    yield isDay ? WeatherType.CLOUDY : WeatherType.CLOUDY_NIGHT;
                } else {
                    yield WeatherType.CLOUDY_MORE;
                }
            }
            default -> WeatherType.CLOUDY_MORE;
        };
    }
}
