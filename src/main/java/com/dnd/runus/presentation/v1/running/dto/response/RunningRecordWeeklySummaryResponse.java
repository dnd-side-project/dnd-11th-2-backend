package com.dnd.runus.presentation.v1.running.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record RunningRecordWeeklySummaryResponse(
    @Schema(description = "이번주 날짜", example = "2024.09.09 ~ 09.15")
    String weeklyDate,
    @Schema(description = "요일 별 활동 값, 거리는 km, 시간은 시간(Hour) 단위<br>"
        + "월요일(index:0) ~ 일요일(index:6)의 값, 기록없는 날은 0을 리턴")
    double[] weeklyValues,
    @Schema(description = "지난주 평균 활동 값, 거리는 km, 시간은 시간(Hour) 단위<br>"
        + "지난주에 기록이 없으면 0을 리턴")
    double lastWeekAvgValue
) {
    public RunningRecordWeeklySummaryResponse(LocalDate startDate, LocalDate endDate, double[] weeklyValues, double lastWeekAvgValue) {
        this(dateFormat(startDate, endDate), weeklyValues, lastWeekAvgValue);
    }

    private static String dateFormat(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedStartDate = startDate.format(yyyyMMddFormatter);
        String formattedEndDate = endDate.format(yyyyMMddFormatter);
        return formattedStartDate + " ~ " + formattedEndDate;
    }
}
