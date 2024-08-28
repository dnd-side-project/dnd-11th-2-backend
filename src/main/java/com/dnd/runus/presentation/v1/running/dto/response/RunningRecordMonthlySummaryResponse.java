package com.dnd.runus.presentation.v1.running.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;

public record RunningRecordMonthlySummaryResponse(
    @Schema(description = "이번 달", example = "8월")
    String month,
    @Schema(description = "이번 달에 달린 키로 수", example = "2.55KM")
    String monthlyKm,
    @Schema(description = "다음 레벨", example = "Leve 2")
    String nextLevelName,
    @Schema(description = "다음 레벨까지 남은 키로 수", example = "2.55km")
    String nextLevelKm
) {
    public RunningRecordMonthlySummaryResponse(int monthValue, int monthlyTotalMeter, String nextLevelName, String nextLevelKm) {
        this(formatToMonth(monthValue), formatMeterToKm(monthlyTotalMeter), nextLevelName, nextLevelKm);
    }

    private static String formatToMonth(int monthValue) {
        return monthValue + "월";
    }

    private static String formatMeterToKm(int meter) {
        String formatted = String.format("%.2f", Math.floor(meter / 1000.0 * 100) / 100);

        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0*$", "");
        }

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + "KM";
    }
}
