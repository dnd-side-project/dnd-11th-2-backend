package com.dnd.runus.infrastructure.weather.openweathermap;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapCurrent;
import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapForecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OpenweathermapWeatherClientTest {
    private OpenweathermapWeatherClient openweathermapWeatherClient;

    @Mock
    private OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient;

    private final String appId = "test";
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void setUp() {
        openweathermapWeatherClient =
                new OpenweathermapWeatherClient(openweathermapWeatherHttpClient, appId, executorService);
    }

    @DisplayName("getWeatherInfo()는 current와 forecast 날씨 정보를 가져온다.")
    @Test
    void getWeatherInfo_success() {
        // given
        double longitude = 126.9780;
        double latitude = 37.5665;
        String unit = "metric";

        given(openweathermapWeatherHttpClient.getWeatherInfo(longitude, latitude, unit, appId))
                .willReturn(new OpenweathermapCurrent(
                        new OpenweathermapCurrent.Weather[] {
                            new OpenweathermapCurrent.Weather(803, "Clouds", "broken clouds", "04d")
                        },
                        new OpenweathermapCurrent.Main(19.17, 19.66, 19.96, 51, 1013, 45),
                        new OpenweathermapCurrent.Wind(8.01, 240, 8.01),
                        1633665600,
                        new OpenweathermapCurrent.Sys(1633620344, 1633662926)));

        int count = 8;
        given(openweathermapWeatherHttpClient.getForecast(longitude, latitude, unit, appId, count))
                .willReturn(new OpenweathermapForecast("200", new OpenweathermapForecast.List[] {
                    new OpenweathermapForecast.List(
                            1633665600, new OpenweathermapForecast.List.Main(16, 16.96, 15, 17, 51, 47, 0)),
                    new OpenweathermapForecast.List(
                            1633676400, new OpenweathermapForecast.List.Main(18, 16.96, 16, 19, 51, 57, 0)),
                    new OpenweathermapForecast.List(
                            1633687200, new OpenweathermapForecast.List.Main(21, 16.96, 18, 22, 51, 56, 0)),
                }));

        // when
        WeatherInfo result = openweathermapWeatherClient.getWeatherInfo(longitude, latitude);

        // then
        WeatherInfo expected = new WeatherInfo(WeatherType.CLOUDY_MORE, 19.66, 15, 22.0, 45, 8.01);
        assertEquals(expected, result);
    }
}
