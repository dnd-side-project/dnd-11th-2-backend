package com.dnd.runus.infrastructure.weather.openweathermap;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface OpenweathermapWeatherHttpClient {
    @GetExchange("/data/2.5/weather")
    OpenweathermapWeatherInfo getWeatherInfo(
            @RequestParam("lon") double lon,
            @RequestParam("lat") double lat,
            @RequestParam String unit,
            @RequestParam("appid") String appId);
}
