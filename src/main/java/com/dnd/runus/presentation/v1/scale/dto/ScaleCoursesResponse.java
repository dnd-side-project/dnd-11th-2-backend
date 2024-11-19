package com.dnd.runus.presentation.v1.scale.dto;

import com.dnd.runus.application.scale.dto.CoursesDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

@Builder
public record ScaleCoursesResponse(
        Info info,
        List<AchievedCourse> achievedCourses,
        CurrentCourse currentCourse
) {

    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("#,###.#km");

    /**
     * 거리 formater
     *
     * @return distanceMeter가 1km보다 작을 경우 "m"로 아니면 "#,###.#km"형식으로 반환합니다.
     */
    private static String formaterForDistance(int distanceMeter) {
        if (distanceMeter < METERS_IN_A_KILOMETER) {
            return distanceMeter + "m";
        }
        return KILO_METER_FORMATTER.format(distanceMeter / METERS_IN_A_KILOMETER);
    }


    private static String makeMessageAboutLeftKm(String goalPoint, int leftMeter) {
        //소수점 둘째자리에서 반올림
        double leftKm = Math.round((leftMeter / METERS_IN_A_KILOMETER) * 10.0) / 10.0;
        return goalPoint + "까지 " +  KILO_METER_FORMATTER.format(leftKm) + " 남았어요!";
    }



    @Schema(name = "courseResponseV1 Info", description = "코스 정보")
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
            this(totalCourses, formaterForDistance(totalMeter));
        }
    }

    @Schema(name = "courseResponseV1 AchievedCourse", description = "달성한 코스")
    public record AchievedCourse(
        @Schema(description = "코스 이름", example = "서울에서 인천")
        String name,
        @Schema(description = "코스 총 거리", example = "30km")
        String totalDistance,
        @Schema(description = "달성 일자")
        LocalDate achievedAt
    ) {
        public static ScaleCoursesResponse.AchievedCourse from(CoursesDto.Course achievedCourse) {
            return new ScaleCoursesResponse.AchievedCourse(
                achievedCourse.name(),
                formaterForDistance(achievedCourse.sizeMeter()),
                achievedCourse.achievedAt().toLocalDate()
            );
        }
    }

    @Schema(name = "courseResponseV1 CurrentCourse", description = "현재 코스")
    public record CurrentCourse(
        @Schema(description = "현재 코스 이름", example = "서울에서 부산")
        String name,
        @Schema(description = "현재 코스 총 거리", example = "200km")
        String totalDistance,
        @Schema(description = "현재 달성한 거리, 현재 32.3km 달성", example = "32.3km")
        String achievedDistance,
        @Schema(description = "현재 코스 설명 메시지", example = "대전까지 100km 남았어요!")
        String message
    ) {
        public CurrentCourse(
            String name,
            int totalDistanceMeter,
            int achievedDistanceMeter,
            String message
        ) {
            this(
                name,
                formaterForDistance(totalDistanceMeter),
                formaterForDistance(achievedDistanceMeter),
                message
            );
        }

        public static ScaleCoursesResponse.CurrentCourse from(CoursesDto.Course course) {
            return new ScaleCoursesResponse.CurrentCourse(
                course.name(),
                formaterForDistance(course.sizeMeter()),
                formaterForDistance(course.achievedMeter()),
                makeMessageAboutLeftKm(
                    course.endName(),
                    Math.max(0, course.sizeMeter() - course.achievedMeter())
                )
            );
        }
    }
}
