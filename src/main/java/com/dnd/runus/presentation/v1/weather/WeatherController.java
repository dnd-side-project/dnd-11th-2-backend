package com.dnd.runus.presentation.v1.weather;

import com.dnd.runus.application.weather.WeatherService;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.weather.dto.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "날씨")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/weathers")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiErrorType({ErrorType.WEATHER_API_ERROR})
    @Operation(summary = "날씨 정보 조회", description = "경도와 위도를 입력받아 날씨 정보를 조회합니다.")
    public WeatherResponse getWeather(@RequestParam double longitude, @RequestParam double latitude) {
        return weatherService.getWeather(longitude, latitude);
    }
}
