package com.dnd.runus.infrastructure.weather.weatherapicom;

import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomCurrent;
import com.dnd.runus.infrastructure.weather.weatherapicom.dto.WeatherapicomHistory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface WeatherapicomWeatherHttpClient {
    @GetExchange("/v1/current.json")
    WeatherapicomCurrent getWeatherInfo(@RequestParam("q") String query, @RequestParam("key") String apiKey);

    @GetExchange("/v1/history.json")
    WeatherapicomHistory getWeatherHistory(
            @RequestParam("q") String query,
            @RequestParam("dt") String datetime,
            @RequestParam int hour,
            @RequestParam("key") String apiKey);
}
