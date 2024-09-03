package com.dnd.runus.presentation.v1.scale.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.text.NumberFormat;
import java.util.Locale;

public record ScaleSummaryResponse(
    @Schema(description = "전체 코스 수", example = "18코스")
    String courseCount,
    @Schema(description = "런어스 총 거리", example = "43,800km")
    String runUsDistanceKm,
    @Schema(description = "지구 한 바퀴 거리", example = "40,075km")
    String earthDistanceKm
) {
    public ScaleSummaryResponse(int totalCourseCnt, int totalCourseDistanceMeter, String earthDistanceKm) {
        this(courseCountFormater(totalCourseCnt), meterToKM(totalCourseDistanceMeter), earthDistanceKm);
    }

    private static String meterToKM(int totalCourseDistanceMeter) {
        double kilometers = totalCourseDistanceMeter / 1000.0;

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setGroupingUsed(true); // 쉼표 구분 사용
        return numberFormat.format(kilometers) + "km";
    }

    private static String courseCountFormater(int totalCourseCnt) {
        return totalCourseCnt + "코스";
    }
}
