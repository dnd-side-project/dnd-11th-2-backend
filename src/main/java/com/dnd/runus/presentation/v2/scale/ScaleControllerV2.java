package com.dnd.runus.presentation.v2.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지구 한 바퀴")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/scale")
public class ScaleControllerV2 {

    private final ScaleService scaleService;

    @GetMapping("courses")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "달성 기록 조회",
            description =
                    """
                    달성 기록의 코스 관련 데이터를 조회합니다.<br>
                    - 나의 달성 정보 : 현재 달린 키로수, 전체 코스 완주 까지 남은 키로수, 코스에 대한 퍼센테이지 값
                    - 달성한 코스 목록, 현재 진행중인 코스 정보, 다음 코스 정보를 반환합니다.<br>
                        - 달성한 코스가 없다면, 빈 리스트를 반환합니다.<br>
                        - 다음 코스 가 없다면 빈 리스트를 반환합니다.
                    """)
    public ScaleCoursesResponseV2 getCourses(@MemberId long memberId) {
        CoursesDto courses = scaleService.getAchievements(memberId);
        boolean isCompleteAll = courses.currentCourse() == null;

        return ScaleCoursesResponseV2.builder()
                .myAchievedStatus(new ScaleCoursesResponseV2.MyAchievedStatus(
                        courses.myTotalRunningMeter(),
                        courses.totalCoursesDistanceMeter(),
                        courses.totalCoursesCount(),
                        isCompleteAll
                                ? courses.totalCoursesCount() + 1
                                : courses.currentCourse().order()))
                .info(new ScaleCoursesResponseV2.Info(courses.totalCoursesCount(), courses.totalCoursesDistanceMeter()))
                .achievedCourses(courses.achievedCourses().stream()
                        .map(ScaleCoursesResponseV2.AchievedCourse::from)
                        .toList())
                .currentCourse(
                        isCompleteAll
                                ? new ScaleCoursesResponseV2.NotAchievedCourse(
                                        "지구 한바퀴",
                                        courses.totalCoursesDistanceMeter(),
                                        "축하합니다! 지구 한바퀴 완주하셨네요!",
                                        courses.totalCoursesCount() + 1)
                                : ScaleCoursesResponseV2.NotAchievedCourse.of(
                                        courses.currentCourse(), courses.myTotalRunningMeter()))
                .nextCourse(
                        courses.nextCourses().isEmpty()
                                ? null
                                : ScaleCoursesResponseV2.NotAchievedCourse.of(
                                        courses.nextCourses().getFirst(), courses.myTotalRunningMeter()))
                .build();
    }
}
