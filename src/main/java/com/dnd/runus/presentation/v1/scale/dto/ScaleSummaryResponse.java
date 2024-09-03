package com.dnd.runus.presentation.v1.scale.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public record ScaleSummaryResponse(
    @Schema(description = "전체 코스 수", example = "18코스")
    String courseCount,
    @Schema(description = "런어스 총 거리", example = "43,800km")
    String runUsDistanceKm,
    @Schema(description = "지구 한 바퀴 거리", example = "40,075km")
    String earthDistanceKm
) {
}
