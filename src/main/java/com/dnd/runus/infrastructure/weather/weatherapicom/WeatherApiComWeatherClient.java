package com.dnd.runus.infrastructure.weather.weatherapicom;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomCurrent;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.dnd.runus.global.exception.type.ErrorType.WEATHER_API_ERROR;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Slf4j
@Component
public class WeatherApiComWeatherClient implements WeatherClient {
    private final WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient;
    private final String apiKey;
    private final ExecutorService executorService;

    public WeatherApiComWeatherClient(
            WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient,
            @Value("${weather.weatherapicom.api-key}") String apiKey,
            @Qualifier("virtualExecutorService") ExecutorService executorService) {
        this.weatherapicomWeatherHttpClient = weatherapicomWeatherHttpClient;
        this.apiKey = apiKey;
        this.executorService = executorService;
    }

    @Override
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        String query = String.format("%f,%f", latitude, longitude);

        Future<WeatherapicomCurrent> currentFuture =
                executorService.submit(() -> weatherapicomWeatherHttpClient.getWeatherInfo(query, apiKey));

        Future<WeatherapicomHistory> historyFuture = executorService.submit(() -> {
            int hour = 0;
            String datetime = LocalDate.now().format(ISO_LOCAL_DATE);
            return weatherapicomWeatherHttpClient.getWeatherHistory(query, datetime, hour, apiKey);
        });

        try {
            WeatherapicomCurrent current = currentFuture.get();
            WeatherapicomHistory history = historyFuture.get();

            double mphToMs = 0.44704;

            WeatherapicomHistory.Forecast.ForecaseDay.DayInfo today =
                    history.forecast().forecastday().getFirst().day();

            return new WeatherInfo(
                    mapWeatherType(current.current().condition().code(), current.isDay()),
                    current.current().feelslikeC(),
                    today.mintempC(),
                    today.maxtempC(),
                    current.current().humidity(),
                    current.current().windMph() * mphToMs);
        } catch (Exception e) {
            throw new BusinessException(
                    WEATHER_API_ERROR,
                    String.format("[weatherapicom] lon: %f, lat: %f, %s", longitude, latitude, e.getMessage()));
        }
    }

    private WeatherType mapWeatherType(int weatherCode, boolean isDay) {
        return switch (weatherCode) {
            case 1000 -> isDay ? WeatherType.CLEAR : WeatherType.CLEAR_NIGHT;
            case 1003, 1006 -> isDay ? WeatherType.CLOUDY : WeatherType.CLOUDY_NIGHT;
            case 1087, 1273, 1276, 1279, 1282 -> WeatherType.STORM;
            case 1063, 1150, 1153, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246 -> WeatherType.RAIN;
            case 1066, 1069, 1072, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1255, 1258, 1261, 1264 -> WeatherType
                    .SNOW;
            case 1135, 1147 -> WeatherType.FOG;

            default -> WeatherType.CLOUDY_MORE;
        };
    }
}
