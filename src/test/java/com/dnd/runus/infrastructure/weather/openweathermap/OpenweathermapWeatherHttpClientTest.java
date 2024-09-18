package com.dnd.runus.infrastructure.weather.openweathermap;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"weather.openweathermap.url=http://localhost:${wiremock.server.port}"})
@RestClientTest({OpenweathermapWeatherHttpClient.class, OpenweathermapWeatherHttpClientConfig.class})
class OpenweathermapWeatherHttpClientTest {

    @Autowired
    private OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
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
        double longitude = 126.9780;
        double latitude = 37.5665;

        Body body = new Body("{\"weather\": ["
                + "    {"
                + "      \"id\": 800,"
                + "      \"main\": \"Clear\","
                + "      \"description\": \"clear sky\","
                + "      \"icon\": \"01n\""
                + "    }"
                + "  ],"
                + "  \"main\": {"
                + "    \"temp\": 10.0,"
                + "    \"feels_like\": 10.0,"
                + "    \"temp_min\": 10.0,"
                + "    \"temp_max\": 10.0,"
                + "    \"pressure\": 1022,"
                + "    \"humidity\": 30"
                + "  },"
                + "  \"wind\": {"
                + "    \"speed\": 1.5,"
                + "    \"deg\": 350"
                + "  },"
                + "  \"clouds\": {"
                + "    \"all\": 0"
                + "  },"
                + "  \"dt\": 1609344000,"
                + "  \"sys\": {"
                + "    \"type\": 1,"
                + "    \"id\": 8105,"
                + "    \"country\": \"KR\","
                + "    \"sunrise\": 1609280340,"
                + "    \"sunset\": 1609314430"
                + "  },"
                + "  \"timezone\": 32400,"
                + "  \"id\": 1835848,"
                + "  \"name\": \"Seoul\","
                + "  \"cod\": 200"
                + "}");
        stubFor(get(urlEqualTo("/data/2.5/weather?lon=126.978&lat=37.5665&units=metric&appid=test"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withResponseBody(body)));

        // when
        OpenweathermapWeatherInfo weatherInfo =
                openweathermapWeatherHttpClient.getWeatherInfo(longitude, latitude, "metric", "test");

        // then
        assertNotNull(weatherInfo);
        assertEquals(800, weatherInfo.weather()[0].id());
        assertEquals(10.0, weatherInfo.main().feelsLike());
        assertEquals(10.0, weatherInfo.main().tempMin());
    }
}
