package com.dnd.runus.infrastructure.weather.openweathermap;

import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapCurrent;
import com.dnd.runus.infrastructure.weather.openweathermap.dto.OpenweathermapForecast;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface OpenweathermapWeatherHttpClient {
    @GetExchange("/data/2.5/weather")
    OpenweathermapCurrent getWeatherInfo(
            @RequestParam("lon") double longitude,
            @RequestParam("lat") double latitude,
            @RequestParam("units") String unit,
            @RequestParam("appid") String appId);

    @GetExchange("/data/2.5/forecast")
    OpenweathermapForecast getForecast(
            @RequestParam("lon") double longitude,
            @RequestParam("lat") double latitude,
            @RequestParam("units") String unit,
            @RequestParam("appid") String appId,
            @RequestParam("cnt") int count);
}
