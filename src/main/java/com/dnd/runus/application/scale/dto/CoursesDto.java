package com.dnd.runus.application.scale.dto;


import com.dnd.runus.domain.scale.ScaleAchievementLog;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CoursesDto(
    int totalCoursesCount,
    int totalCoursesDistanceMeter,
    int myTotalRunningMeter,
    List<Course> achievedCourses,
    Course currentCourse,
    List<Course> nextCourses
) {

    /**
     * 코스 DTO
     * @param order 코스 순서
     * @param achievedMeter 사용자의 코스에 대한 달성 미터 값
     *                      (ex. totalMeter가 1000, requiredMeterForAchieve가 3000,
     *                      사용자가 전체 달린 거리가 2600이라면
     *                      achievedMeter는 600
     *                      )
     * @param sizeMeter 코스의 거리 미터 값
     * @param requiredMeterForAchieve 코스를 달성하기 위한 미터값
     *                                (ex. 2코스라면, 1코스 totalMeter + 2코스 totalMeter의 값)
     * @param achievedAt 달성한 날짜, 달성하지 않으면 null
     */
    @Schema(name = "course", description = "코스")
    public record Course(
        String name,
        String StartName,
        String endName,
        int order,
        int achievedMeter,
        int sizeMeter,
        int requiredMeterForAchieve,
        OffsetDateTime achievedAt
    ) {
        public static Course of(
            ScaleAchievementLog scaleAchievementLog,
            int requiredTotalMeterForAchieve,
            int achievedMeter
            ) {
            return new Course(
                scaleAchievementLog.scale().name(),
                scaleAchievementLog.scale().startName(),
                scaleAchievementLog.scale().endName(),
                scaleAchievementLog.scale().index(),
                achievedMeter,
                scaleAchievementLog.scale().sizeMeter(),
                requiredTotalMeterForAchieve,
                scaleAchievementLog.achievedDate()
            );
        }
    }
}
