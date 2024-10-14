package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.presentation.v1.running.dto.WeeklyRunningRatingDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RunningRecordWeeklySummaryResponse(
    @Schema(description = "이번주 날짜", example = "2024.09.09 ~ 2024.09.15")
    String weeklyDate,
    @Schema(description = "요일 별 활동 값<br>"
        + "월 ~ 일 순서로 리턴")
    List<WeeklyRunningRatingDto> weeklyValues,
    @Schema(description = "지난주 평균 활동 값, 거리는 km, 시간은 시간(Hour) 단위<br>"
        + "지난주에 기록이 없으면 0을 리턴")
    double lastWeekAvgValue
) {
    public RunningRecordWeeklySummaryResponse(LocalDate startDate, LocalDate endDate, List<WeeklyRunningRatingDto> weeklyValues, double lastWeekAvgValue) {
        this(dateFormat(startDate, endDate), weeklyValues, lastWeekAvgValue);
    }

    private static String dateFormat(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedStartDate = startDate.format(yyyyMMddFormatter);
        String formattedEndDate = endDate.format(yyyyMMddFormatter);
        return formattedStartDate + " ~ " + formattedEndDate;
    }
}
