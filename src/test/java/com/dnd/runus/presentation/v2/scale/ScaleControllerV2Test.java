package com.dnd.runus.presentation.v2.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2.AchievedCourse;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2.Info;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2.MyAchievedStatus;
import com.dnd.runus.presentation.v2.scale.dto.response.ScaleCoursesResponseV2.NotAchievedCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ScaleControllerV2Test {
    // 1. 포멧에 대해 확인해보기
    // 2. 하나도 달성하지 못한 경우
    // 3. 전체 코스를 다 달성했을 경우

    @InjectMocks
    private ScaleControllerV2 scaleController;

    @Mock
    private ScaleService scaleService;

    private long memberId;
    private Scale scale1;
    private Scale scale2;
    private Scale scale3;
    private Scale scale4;

    @BeforeEach
    void setUp() {
        memberId = 1;
        scale1 = new Scale(1, "서울에서 인천", 200, 1, "서울", "인천");
        scale2 = new Scale(2, "인천에서 대전", 1000, 2, "인천", "대전");
        scale3 = new Scale(3, "대전에서 대구", 3000, 3, "대전", "대구");
        scale4 = new Scale(4, "서울에서 일본", 1_000_000, 4, "서울", "일본");
    }

    @DisplayName("반환 문구, km 단위 등을 확인합니다.")
    @Test
    void getAchievements() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(450)
                        .achievedCourses(List.of(
                                CoursesDto.Course.of(new ScaleAchievementLog(scale1, now), scale1.sizeMeter(), 200)))
                        .currentCourse(CoursesDto.Course.of(
                                new ScaleAchievementLog(scale2, null), scale1.sizeMeter() + scale2.sizeMeter(), 250))
                        .nextCourses(List.of(
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale3, null),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter(),
                                        0),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale4, null),
                                        scale1.sizeMeter()
                                                + scale2.sizeMeter()
                                                + scale3.sizeMeter()
                                                + scale4.sizeMeter(),
                                        0)))
                        .build());

        // when
        ScaleCoursesResponseV2 response = scaleController.getCourses(memberId);

        // then
        MyAchievedStatus myAchievedStatus = response.myAchievedStatus();
        assertEquals("450m", myAchievedStatus.achievedDistance());
        assertEquals("완주까지 1,003.8km 남았어요!", myAchievedStatus.remainingTotalDistance());
        assertEquals(0.25, myAchievedStatus.percentage());

        Info info = response.info();
        assertEquals(4, info.totalCourses());
        assertEquals("1,004.2km", info.totalDistance());

        AchievedCourse achievedCourse = response.achievedCourses().getFirst();
        assertEquals(scale1.name(), achievedCourse.name());
        assertEquals("200m", achievedCourse.totalDistance());
        assertEquals(now.toLocalDate(), achievedCourse.achievedAt());

        NotAchievedCourse currentCourse = response.currentCourse();
        assertEquals(scale2.name(), currentCourse.name());
        assertEquals("1km", currentCourse.totalDistance());
        assertEquals("0.8km 남았어요!", currentCourse.message());
        assertEquals(2, currentCourse.courseOrder());

        NotAchievedCourse nextCourse = response.nextCourse();
        assertEquals(scale3.name(), nextCourse.name());
        assertEquals("3km", nextCourse.totalDistance());
        assertEquals("3.8km 남았어요!", nextCourse.message());
        assertEquals(3, nextCourse.courseOrder());
    }

    @DisplayName("달성한 코스가 없는 경우, 성취 코스는 빈 리스트를 반환합니다.")
    @Test
    void getAchievements_without_achieved() {
        // given
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(50)
                        .achievedCourses(List.of())
                        .currentCourse(CoursesDto.Course.of(new ScaleAchievementLog(scale1, null), 200, 50))
                        .nextCourses(List.of(
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale2, null),
                                        scale1.sizeMeter() + scale2.sizeMeter(),
                                        0),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale3, null),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter(),
                                        0),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale4, null),
                                        scale1.sizeMeter()
                                                + scale2.sizeMeter()
                                                + scale3.sizeMeter()
                                                + scale4.sizeMeter(),
                                        0)))
                        .build());

        ScaleCoursesResponseV2 response = scaleController.getCourses(memberId);

        assertTrue(response.achievedCourses().isEmpty());

        MyAchievedStatus myAchievedStatus = response.myAchievedStatus();
        assertEquals("50m", myAchievedStatus.achievedDistance());
        assertEquals("완주까지 1,004.2km 남았어요!", myAchievedStatus.remainingTotalDistance());
        assertEquals(0, myAchievedStatus.percentage());

        NotAchievedCourse currentCourse = response.currentCourse();
        assertEquals(scale1.name(), currentCourse.name());
        assertEquals("200m", currentCourse.totalDistance());
        assertEquals("0.2km 남았어요!", currentCourse.message());
        assertEquals(1, currentCourse.courseOrder());

        NotAchievedCourse nextCourse = response.nextCourse();
        assertEquals(scale2.name(), nextCourse.name());
        assertEquals("1km", nextCourse.totalDistance());
        assertEquals("1.2km 남았어요!", nextCourse.message());
        assertEquals(2, nextCourse.courseOrder());
    }

    @DisplayName("현재 코스가 마지막 코스인 경우, 다음 코스는 null 값을 반환합니다.")
    @Test
    void getAchievements_left_no_nextCourse() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(6_500)
                        .achievedCourses(List.of(
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale1, now), scale1.sizeMeter(), scale1.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale2, now),
                                        scale1.sizeMeter() + scale2.sizeMeter(),
                                        scale2.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale3, now),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter(),
                                        scale3.sizeMeter())))
                        .currentCourse(CoursesDto.Course.of(
                                new ScaleAchievementLog(scale4, null),
                                scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter() + scale4.sizeMeter(),
                                2_300))
                        .nextCourses(List.of())
                        .build());

        ScaleCoursesResponseV2 response = scaleController.getCourses(memberId);

        assertNull(response.nextCourse());
        assertEquals(3, response.achievedCourses().size());

        MyAchievedStatus myAchievedStatus = response.myAchievedStatus();
        assertEquals("6.5km", myAchievedStatus.achievedDistance());
        assertEquals("완주까지 997.7km 남았어요!", myAchievedStatus.remainingTotalDistance());
        assertEquals(0.75, myAchievedStatus.percentage());

        NotAchievedCourse currentCourse = response.currentCourse();
        assertEquals(scale4.name(), currentCourse.name());
        assertEquals("1,000km", currentCourse.totalDistance());
        assertEquals("997.7km 남았어요!", currentCourse.message());
        assertEquals(4, currentCourse.courseOrder());
    }

    @DisplayName("전체 코스를 달성할 경우, 다음 코스는 null 값을 반환합니다. 현제 코스는 '지구 한바퀴 완주'관련해서 리턴합니다.")
    @Test
    void getAchievements_completeAll() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(1_004_800)
                        .achievedCourses(List.of(
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale1, now), scale1.sizeMeter(), scale1.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale2, now),
                                        scale1.sizeMeter() + scale2.sizeMeter(),
                                        scale2.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale3, now),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter(),
                                        scale3.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale4, now),
                                        scale1.sizeMeter()
                                                + scale2.sizeMeter()
                                                + scale3.sizeMeter()
                                                + scale4.sizeMeter(),
                                        scale4.sizeMeter())))
                        .nextCourses(List.of())
                        .build());

        ScaleCoursesResponseV2 response = scaleController.getCourses(memberId);

        assertNull(response.nextCourse());
        assertEquals(4, response.achievedCourses().size());

        MyAchievedStatus myAchievedStatus = response.myAchievedStatus();
        assertEquals("1,004.8km", myAchievedStatus.achievedDistance());
        assertEquals("축하합니다! 지구 한바퀴 완주하셨네요!", myAchievedStatus.remainingTotalDistance());
        assertEquals(1, myAchievedStatus.percentage());

        NotAchievedCourse currentCourse = response.currentCourse();
        assertEquals("지구 한바퀴", currentCourse.name());
        assertEquals("1,004.2km", currentCourse.totalDistance());
        assertEquals("축하합니다! 지구 한바퀴 완주하셨네요!", currentCourse.message());
        assertEquals(5, currentCourse.courseOrder());
    }
}
