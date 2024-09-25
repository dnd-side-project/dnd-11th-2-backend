package com.dnd.runus.auth.oidc.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class RestClientConfig {

    @Value("${spring.threads.virtual.enabled}")
    private boolean isVirtualThreadEnabled;

    private static final Duration READ_TIMEOUT = Duration.ofSeconds(1);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);

    @Bean
    AppleAuthClient appleAuthClient(@Value("${oauth.apple.base-auth-url}") String baseAuthUrl) {
        ClientHttpRequestFactory requestFactory;

        if (isVirtualThreadEnabled) {
            requestFactory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                    .connectTimeout(CONNECT_TIMEOUT)
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .build());
        } else {
            requestFactory = ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                    .withReadTimeout(READ_TIMEOUT)
                    .withConnectTimeout(CONNECT_TIMEOUT));
        }

        RestClient restClient = RestClient.builder()
                .baseUrl(baseAuthUrl)
                .requestFactory(requestFactory)
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(AppleAuthClient.class);
    }
}
