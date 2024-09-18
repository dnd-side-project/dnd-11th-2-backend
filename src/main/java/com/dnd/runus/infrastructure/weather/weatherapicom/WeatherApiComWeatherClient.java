package com.dnd.runus.infrastructure.weather.weatherapicom;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.infrastructure.weather.WeatherClient;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomCurrent;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.dnd.runus.global.exception.type.ErrorType.WEATHER_API_ERROR;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class WeatherApiComWeatherClient implements WeatherClient {
    private final WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient;

    @Value("${weather.weatherapicom.api-key}")
    private String apiKey;

    @Override
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        String query = String.format("%f,%f", latitude, longitude);

        WeatherapicomCurrent currentInfo = weatherapicomWeatherHttpClient.getWeatherInfo(query, apiKey);
        if (currentInfo == null || currentInfo.current() == null) {
            throw new BusinessException(WEATHER_API_ERROR, "[weatherapicom] 현재 날씨 정보를 가져올 수 없습니다.");
        }

        String datetime = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        WeatherapicomHistory historyInfo = weatherapicomWeatherHttpClient.getWeatherHistory(query, datetime, 0, apiKey);

        if (historyInfo == null
                || historyInfo.forecast() == null
                || isEmpty(historyInfo.forecast().forecastday())) {
            throw new BusinessException(WEATHER_API_ERROR, "[weatherapicom] 과거 날씨 정보를 가져올 수 없습니다.");
        }

        double mphToMs = 0.44704;
        return new WeatherInfo(
                mapWeatherType(currentInfo.current().condition().code()),
                currentInfo.current().feelslikeC(),
                historyInfo.forecast().forecastday().get(0).day().mintempC(),
                historyInfo.forecast().forecastday().get(0).day().maxtempC(),
                currentInfo.current().humidity(),
                currentInfo.current().windMph() * mphToMs);
    }

    private WeatherType mapWeatherType(int weatherCode) {
        return switch (weatherCode) {
            case 1000 -> WeatherType.CLEAR;
            case 1003, 1006 -> WeatherType.CLOUDY;
            case 1087, 1273, 1276, 1279, 1282 -> WeatherType.STORM;
            case 1063, 1150, 1153, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246 -> WeatherType.RAIN;
            case 1066, 1069, 1072, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1255, 1258, 1261, 1264 -> WeatherType
                    .SNOW;
            case 1135, 1147 -> WeatherType.FOG;

            default -> WeatherType.CLOUDY_MORE;
        };
    }
}
