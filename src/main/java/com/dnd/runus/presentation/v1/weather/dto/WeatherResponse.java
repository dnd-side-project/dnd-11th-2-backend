package com.dnd.runus.presentation.v1.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WeatherResponse(
        @Schema(description = "날씨 이름", example = "맑음")
        String weatherName,
        @Schema(description = "날씨 안내 문구", example = "해가 쨍쨍한 날씨입니다.")
        String weatherDescription,
        @Schema(description = "날씨 아이콘 URL", format = "uri")
        String weatherIconUrl,
        @Schema(description = "체감온도 (°C)", example = "23")
        int apparentTemperature,
        @Schema(description = "최저기온 (°C)", example = "20")
        int minTemperature,
        @Schema(description = "최고기온 (°C)", example = "25")
        int maxTemperature
) {
}
