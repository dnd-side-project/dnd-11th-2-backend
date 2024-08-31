package com.dnd.runus.presentation.v1.weather;

import com.dnd.runus.application.weather.WeatherService;
import com.dnd.runus.presentation.v1.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/weathers")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WeatherResponse getWeather(double longitude, double latitude) {
        return weatherService.getWeather(longitude, latitude);
    }
}
