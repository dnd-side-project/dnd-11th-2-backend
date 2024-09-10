package com.dnd.runus.presentation.v1.scale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public record ScaleCoursesResponse(
        Info info,
        List<AchievedCourse> achievedCourses,
        CurrentCourse currentCourse
) {

    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("0.#km");

    public record Info(
            @Schema(description = "총 코스 수 (공개되지 않은 코스 포함)", example = "18")
            int totalCourses,
            @Schema(description = "총 코스 거리 (공개되지 않은 코스 포함)", example = "1000km")
            String totalDistance
    ) {
        public Info(
                int totalCourses,
                int totalMeter
        ) {
            this(totalCourses, KILO_METER_FORMATTER.format(totalMeter / 1000.0));
        }
    }

    public record AchievedCourse(
            @Schema(description = "코스 이름", example = "서울에서 인천")
            String name,
            @Schema(description = "코스 총 거리", example = "30km")
            String totalDistance,
            @Schema(description = "달성 일자")
            LocalDate achievedAt
    ) {
        public AchievedCourse(
                String name,
                int totalMeter,
                LocalDate achievedAt
        ) {
            this(name, KILO_METER_FORMATTER.format(totalMeter / 1000.0), achievedAt);
        }
    }

    public record CurrentCourse(
            @Schema(description = "현재 코스 이름", example = "서울에서 부산")
            String name,
            @Schema(description = "현재 코스 총 거리", example = "200km")
            String totalDistance,
            @Schema(description = "현재 달성한 거리, 현재 50m 달성", example = "50m")
            String achievedDistance,
            @Schema(description = "현재 코스 설명 메시지", example = "대전까지 100km 남았어요!")
            String message
    ) {
        public CurrentCourse(
                String name,
                int totalMeter,
                int achievedMeter,
                String message
        ) {
            this(name, KILO_METER_FORMATTER.format(totalMeter / 1000.0), achievedMeter + "m", message);
        }
    }
}
