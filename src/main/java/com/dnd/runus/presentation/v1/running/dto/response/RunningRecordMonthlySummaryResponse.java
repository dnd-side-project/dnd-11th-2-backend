package com.dnd.runus.presentation.v1.running.dto.response;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

import io.swagger.v3.oas.annotations.media.Schema;

import java.text.DecimalFormat;

public record RunningRecordMonthlySummaryResponse(
        @Schema(description = "이번 달", example = "8월")
        String month,
        @Schema(description = "이번 달에 달린 키로 수", example = "2.55km")
        String monthlyKm,
        @Schema(description = "다음 레벨", example = "Level 2")
        String nextLevelName,
        @Schema(description = "다음 레벨까지 남은 키로 수", example = "2.55km")
        String nextLevelKm
) {
    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("0.##km");

    public RunningRecordMonthlySummaryResponse(int monthValue, int monthlyTotalMeter, String nextLevelName, String nextLevelKm) {
        this(monthValue + "월", KILO_METER_FORMATTER.format(monthlyTotalMeter / METERS_IN_A_KILOMETER), nextLevelName, nextLevelKm);
    }
}
