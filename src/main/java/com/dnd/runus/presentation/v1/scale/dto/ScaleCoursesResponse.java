package com.dnd.runus.presentation.v1.scale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record ScaleCoursesResponse(
        Info info,
        List<AchievedCourse> achievedCourses,
        CurrentCourse currentCourse
) {
    public record Info(
            @Schema(description = "총 코스 수 (공개되지 않은 코스 포함)", example = "18")
            int totalCourses,
            @Schema(description = "총 런어스 거리 (미터)", example = "43800000")
            int totalMeter
    ) {
    }

    public record AchievedCourse(
            @Schema(description = "코스 이름", example = "서울에서 부산")
            String name,
            @Schema(description = "코스 총 거리 (미터)", example = "300000")
            int meter,
            @Schema(description = "달성 일자")
            LocalDate achievedAt
    ) {
    }

    public record CurrentCourse(
            @Schema(description = "현재 코스 이름", example = "서울에서 부산")
            String name,
            @Schema(description = "현재 코스 총 거리 (미터)", example = "300000")
            int totalMeter,
            @Schema(description = "현재 달성한 거리 (미터), 현재 50m 달성", example = "50")
            int achievedMeter,
            @Schema(description = "현재 코스 설명 메시지", example = "대전까지 100km 남았어요!")
            String message
    ) {
    }
}
