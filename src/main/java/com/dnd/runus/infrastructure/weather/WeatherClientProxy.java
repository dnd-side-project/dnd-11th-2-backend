package com.dnd.runus.infrastructure.weather;

import com.dnd.runus.global.constant.WeatherType;
import com.dnd.runus.global.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static com.dnd.runus.global.constant.CacheType.Name.WEATHER;

@Slf4j
@Primary
@Component
public class WeatherClientProxy implements WeatherClient {

    private final WeatherClient mainWeatherClient;
    private final WeatherClient fallbackWeatherClient;

    public WeatherClientProxy(
            @Qualifier("openweathermapWeatherClient") WeatherClient openweathermapWeatherClient,
            @Qualifier("weatherApiComWeatherClient") WeatherClient weatherApiComWeatherClient) {
        this.mainWeatherClient = openweathermapWeatherClient;
        this.fallbackWeatherClient = weatherApiComWeatherClient;
    }

    @Override
    @Cacheable(cacheNames = WEATHER, key = "#longitude + ':' + #latitude")
    @CircuitBreaker(name = "weatherClient", fallbackMethod = "fallback")
    public WeatherInfo getWeatherInfo(double longitude, double latitude) {
        return mainWeatherClient.getWeatherInfo(longitude, latitude);
    }

    public WeatherInfo fallback(double longitude, double latitude, BusinessException e) {
        log.error("Business exception occurred. type: {}, message: {}", e.getType(), e.getMessage());
        return fallbackWeatherClient.getWeatherInfo(longitude, latitude);
    }

    public WeatherInfo fallback(double longitude, double latitude, CallNotPermittedException e) {
        log.error("Circuit breaker is open. {}", e.getMessage());
        return fallbackWeatherClient.getWeatherInfo(longitude, latitude);
    }

    public WeatherInfo fallback(Exception e) {
        log.error("Fallback occurred. {}", e.getMessage());
        return new WeatherInfo(WeatherType.CLOUDY, 0, 0, 0, 0, 0);
    }
}
