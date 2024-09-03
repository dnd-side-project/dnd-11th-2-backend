package com.dnd.runus.domain.scale;

import java.text.NumberFormat;
import java.util.Locale;

import static com.dnd.runus.global.constant.ScaleConstant.DISTANCE_KM_AROUND_THE_EARTH;

public record ScaleSummary(String totalCourseCnt, String totalCourseDistanceKm, String earthDistanceKm) {

    public ScaleSummary(int totalCourse, int totalCourseDistanceMeter) {
        this(courseCountFormater(totalCourse), meterToKM(totalCourseDistanceMeter), DISTANCE_KM_AROUND_THE_EARTH);
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
