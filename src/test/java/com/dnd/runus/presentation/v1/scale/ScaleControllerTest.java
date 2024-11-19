package com.dnd.runus.presentation.v1.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse.CurrentCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ScaleControllerTest {

    @InjectMocks
    private ScaleController scaleController;

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
        scale4 = new Scale(4, "서울에서 일본", 1_000_000, 5, "서울", "일본");
    }

    @DisplayName("반환 문구, km 단위 등을 확인합니다.")
    @Test
    void getAchievements() {
        // given
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(450)
                        .achievedCourses(List.of(CoursesDto.Course.of(
                                new ScaleAchievementLog(scale1, OffsetDateTime.now()), scale1.sizeMeter(), 200)))
                        .currentCourse(CoursesDto.Course.of(
                                new ScaleAchievementLog(scale2, null), scale1.sizeMeter() + scale2.sizeMeter(), 250))
                        .build());

        // when
        ScaleCoursesResponse response = scaleController.getCourses(memberId);

        // 1. info 검증
        ScaleCoursesResponse.Info info = response.info();
        assertNotNull(info);
        assertEquals(4, info.totalCourses());
        // #,###.#km 포멧 확인
        assertEquals("1,004.2km", info.totalDistance());

        // 2. current Course 확인
        ScaleCoursesResponse.CurrentCourse currentCourse = response.currentCourse();
        assertNotNull(currentCourse);
        assertEquals(scale2.name(), currentCourse.name());
        assertEquals("1km", currentCourse.totalDistance());
        assertEquals("250m", currentCourse.achievedDistance());
        // 남은 거리가 1km미만일 경우, 소숫점 둘째자리에서 반올림
        assertEquals("대전까지 0.8km 남았어요!", currentCourse.message());

        // 3. achieved 확인
        List<ScaleCoursesResponse.AchievedCourse> achievedCourses = response.achievedCourses();
        assertFalse(achievedCourses.isEmpty());
        ScaleCoursesResponse.AchievedCourse achievedCourse = achievedCourses.getFirst();
        assertEquals(scale1.name(), achievedCourse.name());
        assertEquals("200m", achievedCourse.totalDistance());
        assertEquals(LocalDate.now(), achievedCourse.achievedAt());
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
                        .build());

        // when
        ScaleCoursesResponse response = scaleController.getCourses(memberId);

        // 1. achieved는 빈 리스트
        assertTrue(response.achievedCourses().isEmpty());

        // 2. current Course 확인
        ScaleCoursesResponse.CurrentCourse currentCourse = response.currentCourse();
        assertNotNull(currentCourse);
        assertEquals(scale1.name(), currentCourse.name());
        assertEquals("200m", currentCourse.totalDistance());
        assertEquals("50m", currentCourse.achievedDistance());
        assertEquals(scale1.endName() + "까지 0.2km 남았어요!", currentCourse.message());
    }

    @DisplayName("전체 코스를 달성했을 경우, 현재 코스는 지구한바퀴로 리턴합니다.")
    @Test
    void getAchievements_all_achieved() {
        // given
        given(scaleService.getAchievements(memberId))
                .willReturn(CoursesDto.builder()
                        .totalCoursesCount(4)
                        .totalCoursesDistanceMeter(1_004_200)
                        .myTotalRunningMeter(1_004_550)
                        .achievedCourses(List.of(
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale1, OffsetDateTime.now()),
                                        scale1.sizeMeter(),
                                        scale1.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale2, OffsetDateTime.now()),
                                        scale1.sizeMeter() + scale2.sizeMeter(),
                                        scale1.sizeMeter() + scale2.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale3, OffsetDateTime.now()),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter(),
                                        scale1.sizeMeter() + scale2.sizeMeter() + scale3.sizeMeter()),
                                CoursesDto.Course.of(
                                        new ScaleAchievementLog(scale4, OffsetDateTime.now()),
                                        scale1.sizeMeter()
                                                + scale2.sizeMeter()
                                                + scale3.sizeMeter()
                                                + scale4.sizeMeter(),
                                        scale1.sizeMeter()
                                                + scale2.sizeMeter()
                                                + scale3.sizeMeter()
                                                + scale4.sizeMeter())))
                        .build());

        // when
        ScaleCoursesResponse response = scaleController.getCourses(memberId);
        assertEquals(4, response.achievedCourses().size());

        CurrentCourse currentCourse = response.currentCourse();
        assertNotNull(currentCourse);
        assertEquals("지구 한바퀴", currentCourse.name());
        assertEquals("1,004.2km", currentCourse.totalDistance());
        assertEquals("1,004.5km", currentCourse.achievedDistance());
        assertEquals("축하합니다! 지구 한바퀴 완주하셨네요!", currentCourse.message());
    }
}
