package com.dnd.runus.infrastructure.weather.weatherapicom;

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

@Configuration
public class WeatherapicomWeatherHttpClientConfig {
    @Value("${weather.weatherapicom.url}")
    private String url;

    @Bean
    public WeatherapicomWeatherHttpClient weatherapicomWeatherHttpClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(1))
                .withConnectTimeout(Duration.ofSeconds(3));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        RestClient restClient =
                RestClient.builder().baseUrl(url).requestFactory(requestFactory).build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(WeatherapicomWeatherHttpClient.class);
    }
}
