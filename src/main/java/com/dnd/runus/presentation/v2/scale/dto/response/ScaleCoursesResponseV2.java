package com.dnd.runus.presentation.v2.scale.dto.response;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

import com.dnd.runus.application.scale.dto.CoursesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ScaleCoursesResponseV2(
    @NotNull
    MyAchievedStatus myAchievedStatus,
    @NotNull
    Info info,
    @Schema(description = "달성한 코스 리스트, 달성한 코스가 없으면 빈 리스트 반환")
    List<AchievedCourse> achievedCourses,
    @Schema(description = "현재 코스")
    NotAchievedCourse currentCourse,
    @Schema(description = "다음 코스, 다음 코스가 없을 경우, null값으로 반환합니다.")
    NotAchievedCourse nextCourse
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
        String message = "";
        if (goalPoint != null) {
            message += goalPoint +"까지 ";
        }
        //소수점 둘째자리에서 올림
        double leftKm = Math.round((leftMeter / METERS_IN_A_KILOMETER) * 10.0) / 10.0;
        return message + KILO_METER_FORMATTER.format(leftKm) + " 남았어요!";
    }

    private static double calPercentage(double part, double total) {
        if (total <= 0) return 0;
        return part / total;
    }

    @Schema(name = "courseResponseV2 MyAchievedStatus", description = "나의 지구한바퀴 코스 달성 현황")
    public record MyAchievedStatus(
        @Schema(description = "현재 달성한 거리(전체 달린 거리), 현재 32.3km 달성", example = "32.3km")
        String achievedDistance,
        @Schema(description = "코스 전체 완주까지 남은 거리, 완주까지 ...km 남았어요!, <br>"
            + "전체 코스를 다 완주했을 경우, '축하합니다! 지구 한바퀴 완주하셨네요!'로 리턴됩니다.", example = "완주까지 42,812.4km 남았어요!")
        String remainingTotalDistance,
        @Schema(description = "퍼센테이지 값", example = "0.785")
        double percentage
    ) {
        public MyAchievedStatus(
            int achievedDistanceMeter,
            int totalCoursesDistance,
            int totalCoursesCount,
            int currentCourseIdx
        ) {
            ////todo 완주 시나리오가 명확해지면 다시 작업하기
            //totalCoursesDistance <= achievedDistanceMeter 는 전체 코스 완주했을 경우
            this(
                formaterForDistance(achievedDistanceMeter),
                totalCoursesDistance <= achievedDistanceMeter ?
                    "축하합니다! 지구 한바퀴 완주하셨네요!" :
                    makeMessageAboutLeftKm("완주", totalCoursesDistance - achievedDistanceMeter),
                Math.min(1, calPercentage(currentCourseIdx -1, totalCoursesCount))
            );
        }
    }


    @Schema(name = "courseResponseV2 Info", description = "코스 정보")
    public record Info(
            @Schema(description = "총 코스 수 (공개되지 않은 코스 포함)", example = "18")
            int totalCourses,
            @Schema(description = "총 코스 거리 (공개되지 않은 코스 포함)", example = "1,000km")
            String totalDistance
    ) {
        public Info(
                int totalCourses,
                int totalMeter
        ) {
            this(totalCourses, formaterForDistance(totalMeter));
        }
    }

    @Schema(name = "courseResponseV2 AchievedCourse", description = "달성한 코스")
    public record AchievedCourse(
            @Schema(description = "코스 이름", example = "서울에서 인천")
            String name,
            @Schema(description = "코스 총 거리", example = "30km")
            String totalDistance,
            @Schema(description = "달성 일자")
            LocalDate achievedAt
    ) {
        public static AchievedCourse from(CoursesDto.Course achievedCourse) {
            return new AchievedCourse(
                achievedCourse.name(),
                formaterForDistance(achievedCourse.sizeMeter()),
                achievedCourse.achievedAt().toLocalDate()
            );
        }
    }

    @Schema(name = "courseResponseV2 NotAchievedCourse", description = "달성하지 못한 코스")
    public record NotAchievedCourse(
            @Schema(description = "현재 코스 이름", example = "서울에서 부산")
            String name,
            @Schema(description = "현재 코스 총 거리", example = "200km")
            String totalDistance,
            @Schema(description = "현재 코스 설명 메시지", example = "100km 남았어요!")
            String message,
            @Schema(description = "현재 코스가 몇번째 코스인지나타내는 순서 값", example = "2")
            int courseOrder
    ) {
        public NotAchievedCourse(
            String name,
            int totalDistanceMeter,
            String message,
            int courseOrder
        ) {
            this(
                name,
                formaterForDistance(totalDistanceMeter),
                message,
                courseOrder
            );
        }

        public static NotAchievedCourse of(CoursesDto.Course course, int totalMyRunningMeter) {
            if (course == null) return null;
            return new NotAchievedCourse(
                course.name(),
                formaterForDistance(course.sizeMeter()),
                makeMessageAboutLeftKm(
                    null,
                    //nextCourse 계산을 위해 requiredMeterForAchieve - 전체 달린 거리 빼기
                    Math.max(0, course.requiredMeterForAchieve() - totalMyRunningMeter)
                ),
                course.order()
            );
        }
    }
}
