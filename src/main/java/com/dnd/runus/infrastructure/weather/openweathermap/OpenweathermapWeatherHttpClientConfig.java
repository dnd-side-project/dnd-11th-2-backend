package com.dnd.runus.infrastructure.weather.openweathermap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class OpenweathermapWeatherHttpClientConfig {
    @Value("${weather.openweathermap.url}")
    private String url;

    @Value("${weather.openweathermap.api-key}")
    private String apiKey;

    @Bean
    public OpenweathermapWeatherHttpClient openweathermapWeatherHttpClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(5))
                .withConnectTimeout(Duration.ofSeconds(10));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .defaultUriVariables(Map.of("appid", apiKey))
                .requestFactory(requestFactory)
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(OpenweathermapWeatherHttpClient.class);
    }
}
