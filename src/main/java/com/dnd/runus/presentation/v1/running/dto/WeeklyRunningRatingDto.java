package com.dnd.runus.presentation.v1.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WeeklyRunningRatingDto(
    @Schema(description = "요일", example = "월")
    String day,
    @Schema(description = "거리는 km, 시간은 시간(Hour) 단위, 기록없으면 0")
    double rating
) {
}
