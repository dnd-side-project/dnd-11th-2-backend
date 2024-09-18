package com.dnd.runus.infrastructure.weather.weatherapicom;

import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomCurrent;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomHistory;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"weather.weatherapicom.url=http://localhost:${wiremock.server.port}"})
@RestClientTest({WeatherapicomWeatherHttpClient.class, WeatherapicomWeatherHttpClientConfig.class})
class WeatherapicomWeatherHttpClientTest {

    @Autowired
    private WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient;

    private WireMockServer wireMockServer;

    private String apiKey;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        apiKey = "testApiKey";
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
        wireMockServer.stop();
    }

    @Test
    @DisplayName("좌표 정보로 날씨 정보를 조회할때, 해당 위치의 날씨 정보를 반환한다.")
    void getWeatherInfo() {
        // given
        String response =
                """
                {
                  "location": {
                    "name": "Seoul",
                    "region": "",
                    "country": "South Korea",
                    "lat": 37.57,
                    "lon": 126.98,
                    "tz_id": "Asia/Seoul",
                    "localtime_epoch": 1726644559,
                    "localtime": "2024-09-18 16:29"
                  },
                  "current": {
                    "temp_c": 33.1,
                    "is_day": 1,
                    "condition": {
                      "text": "Partly cloudy",
                      "code": 1003
                    },
                    "wind_mph": 4.7,
                    "wind_kph": 7.6,
                    "wind_degree": 287,
                    "wind_dir": "WNW",
                    "humidity": 67,
                    "cloud": 50,
                    "feelslike_c": 39.6
                  }
                }
                """;

        double longitude = 126.9780;
        double latitude = 37.5665;

        stubFor(get(urlEqualTo("/v1/current.json?q=37.566500%2C126.978000&key=" + apiKey))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(response)));

        // when
        WeatherapicomCurrent currentInfo =
                weatherapicomWeatherHttpClient.getWeatherInfo(String.format("%f,%f", latitude, longitude), apiKey);

        // then
        assertThat(currentInfo).isNotNull();
        assertThat(currentInfo.current().tempC()).isEqualTo(33.1);
        assertThat(currentInfo.current().isDay()).isEqualTo(1);
        assertThat(currentInfo.current().condition().code()).isEqualTo(1003);
        assertThat(currentInfo.current().windMph()).isEqualTo(4.7);
        assertThat(currentInfo.current().humidity()).isEqualTo(67);
        assertThat(currentInfo.current().cloud()).isEqualTo(50);
        assertThat(currentInfo.current().feelslikeC()).isEqualTo(39.6);
    }

    @Test
    @DisplayName("좌표 정보와 날짜, 시간 정보로 날씨 정보를 조회할때, 해당 위치의 최고, 최저 기온 정보를 반환한다.")
    void getWeatherHistory() {
        // given
        String response =
                """
                {
                  "location": {
                    "name": "Seoul",
                    "region": "",
                    "country": "South Korea",
                    "lat": 37.57,
                    "lon": 126.98,
                    "tz_id": "Asia/Seoul"
                  },
                  "forecast": {
                    "forecastday": [
                      {
                        "date": "2024-09-18",
                        "date_epoch": 1726617600,
                        "day": {
                          "maxtemp_c": 29.8,
                          "mintemp_c": 25.1,
                          "totalsnow_cm": 0,
                          "daily_will_it_rain": 1,
                          "daily_chance_of_rain": 100,
                          "daily_will_it_snow": 0,
                          "daily_chance_of_snow": 0
                        }
                      }
                    ]
                  }
                }
                """;

        double longitude = 126.9780;
        double latitude = 37.5665;

        int hour = 14;
        String datetime = LocalDate.of(2024, 9, 18).format(DateTimeFormatter.ISO_LOCAL_DATE);

        stubFor(get(urlEqualTo("/v1/history.json?q=37.566500%2C126.978000&dt=" + datetime + "&hour=" + hour + "&key="
                        + apiKey))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(response)));

        // when
        WeatherapicomHistory historyInfo = weatherapicomWeatherHttpClient.getWeatherHistory(
                String.format("%f,%f", latitude, longitude), datetime, hour, apiKey);

        // then
        WeatherapicomHistory.Forecast.ForecaseDay day =
                historyInfo.forecast().forecastday().get(0);

        assertThat(historyInfo).isNotNull();
        assertThat(historyInfo.location().name()).isEqualTo("Seoul");
        assertThat(day.date()).isEqualTo("2024-09-18");
        assertThat(day.day().maxtempC()).isEqualTo(29.8);
        assertThat(day.day().mintempC()).isEqualTo(25.1);
        assertThat(day.day().totalsnowCm()).isEqualTo(0);
        assertThat(day.day().dailyWillItRain()).isEqualTo(1);
        assertThat(day.day().dailyChanceOfRain()).isEqualTo(100);
        assertThat(day.day().dailyWillItSnow()).isEqualTo(0);
        assertThat(day.day().dailyChanceOfSnow()).isEqualTo(0);
    }
}
