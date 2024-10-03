package com.dnd.runus.infrastructure.weather.openweathermap;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapCurrent;
import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapForecast;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.dnd.runus.global.exception.type.ErrorType.WEATHER_API_ERROR;

@Component
public class OpenweathermapWeatherClient implements WeatherClient {
    private final OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient;
    private final String apiKey;
    private final ExecutorService executorService;

    public OpenweathermapWeatherClient(
            OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient,
            @Value("${weather.openweathermap.api-key}") String apiKey,
            @Qualifier("virtualExecutorService") ExecutorService executorService) {
        this.openweathermapWeatherHttpClient = openweathermapWeatherHttpClient;
        this.apiKey = apiKey;
        this.executorService = executorService;
    }

    @Override
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        String unit = "metric";

        Future<OpenweathermapCurrent> currentFuture = executorService.submit(
                () -> openweathermapWeatherHttpClient.getWeatherInfo(longitude, latitude, unit, apiKey));

        Future<OpenweathermapForecast> forecastFuture = executorService.submit(() -> {
            int count = 8;
            return openweathermapWeatherHttpClient.getForecast(longitude, latitude, unit, apiKey, count);
        });

        try {
            OpenweathermapCurrent current = currentFuture.get();
            OpenweathermapForecast forecast = forecastFuture.get();

            return new WeatherInfo(
                    mapWeatherType(current.weather()[0].id(), current.isDay()),
                    current.main().feelsLike(),
                    forecast.findMinTemp(),
                    forecast.findMaxTemp(),
                    current.main().humidity(),
                    current.wind().speed());
        } catch (Exception e) {
            throw new BusinessException(
                    WEATHER_API_ERROR,
                    String.format("[openweathermap] lon: %f, lat: %f, %s", longitude, latitude, e.getMessage()));
        }
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
