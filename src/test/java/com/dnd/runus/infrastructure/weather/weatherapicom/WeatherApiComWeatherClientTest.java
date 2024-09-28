package com.dnd.runus.infrastructure.weather.weatherapicom;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.infrastructure.weather.WeatherInfo;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomCurrent;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WeatherApiComWeatherClientTest {
    private WeatherApiComWeatherClient weatherApiComWeatherClient;

    @Mock
    private WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final String apkKey = "test";

    @BeforeEach
    void setUp() {
        weatherApiComWeatherClient =
                new WeatherApiComWeatherClient(weatherapicomWeatherHttpClient, apkKey, executorService);
    }

    @Test
    @DisplayName("getWeatherInfo()는 current와 history 날씨 정보를 가져온다.")
    void getWeatherInfo_success() {
        // given
        double longitude = 10.10;
        double latitude = 34.10;

        String query = String.format("%f,%f", latitude, longitude);

        given(weatherapicomWeatherHttpClient.getWeatherInfo(query, apkKey))
                .willReturn(new WeatherapicomCurrent(new WeatherapicomCurrent.Current(
                        10.0, 50.0, 1, new WeatherapicomCurrent.Current.Condition(1000), 10.0, 50, 10, 10.0)));

        String datetime = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        given(weatherapicomWeatherHttpClient.getWeatherHistory(query, datetime, 0, apkKey))
                .willReturn(new WeatherapicomHistory(
                        new WeatherapicomHistory.Location("Seoul", "Seoul", "Korea"),
                        new WeatherapicomHistory.Forecast(List.of(new WeatherapicomHistory.Forecast.ForecaseDay(
                                datetime,
                                new WeatherapicomHistory.Forecast.ForecaseDay.DayInfo(
                                        15.0, 41.0, 5.0, 32.0, 0.0, 0, 0, 0, 0))))));

        // when
        WeatherInfo result = weatherApiComWeatherClient.getWeatherInfo(longitude, latitude);

        // then
        assertNotNull(result);
        assertEquals(new WeatherInfo(WeatherType.CLEAR, 10.0, 5.0, 15.0, 50, 4.4704), result);
    }

    @Test
    @DisplayName("getWeatherInfo()는 current나 history 날씨 정보를 가져오지 못할 경우 BusinessException을 던진다.")
    void getWeatherInfo_history_api_fail() {
        // given
        double longitude = 10.10;
        double latitude = 34.10;

        String query = String.format("%f,%f", latitude, longitude);

        given(weatherapicomWeatherHttpClient.getWeatherInfo(query, apkKey))
                .willReturn(new WeatherapicomCurrent(new WeatherapicomCurrent.Current(
                        10.0, 50.0, 1, new WeatherapicomCurrent.Current.Condition(1000), 10.0, 50, 10, 10.0)));

        String datetime = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        given(weatherapicomWeatherHttpClient.getWeatherHistory(query, datetime, 0, apkKey))
                .willThrow(new IllegalStateException());

        // when & then
        assertThrows(BusinessException.class, () -> weatherApiComWeatherClient.getWeatherInfo(longitude, latitude));
    }
}
