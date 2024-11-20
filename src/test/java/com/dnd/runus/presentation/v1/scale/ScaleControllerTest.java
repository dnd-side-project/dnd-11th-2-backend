package com.dnd.runus.presentation.v1.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.presentation.config.ControllerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithMockUser
@WebMvcTest(ScaleController.class)
class ScaleControllerTest extends ControllerTestHelper {

    @Autowired
    private ScaleController scaleController;

    @MockBean
    private ScaleService scaleService;

    private long memberId;
    private Scale scale1;
    private Scale scale2;
    private Scale scale3;
    private Scale scale4;

    @BeforeEach
    void setUp() {
        setUpMockMvc(scaleController);

        memberId = 1;
        scale1 = new Scale(1, "서울에서 인천", 200, 1, "서울", "인천");
        scale2 = new Scale(2, "인천에서 대전", 1000, 2, "인천", "대전");
        scale3 = new Scale(3, "대전에서 대구", 3000, 3, "대전", "대구");
        scale4 = new Scale(4, "서울에서 일본", 1_000_000, 5, "서울", "일본");
    }

    @DisplayName("반환 문구, km 단위 등을 확인합니다.")
    @Test
    void getAchievements() throws Exception {
        // given
        OffsetDateTime time =
                OffsetDateTime.of(2021, 1, 1, 10, 0, 0, 0, OffsetDateTime.now().getOffset());

        CoursesDto coursesDto = CoursesDto.builder()
                .totalCoursesCount(4)
                .totalCoursesDistanceMeter(1_004_200)
                .myTotalRunningMeter(450)
                .achievedCourses(
                        List.of(CoursesDto.Course.of(new ScaleAchievementLog(scale1, time), scale1.sizeMeter(), 200)))
                .currentCourse(CoursesDto.Course.of(
                        new ScaleAchievementLog(scale2, null), scale1.sizeMeter() + scale2.sizeMeter(), 250))
                .build();

        given(scaleService.getAchievements(memberId)).willReturn(coursesDto);

        // when
        ResultActions result = mvc.perform(get("/api/v1/scale/courses").param("memberId", String.valueOf(memberId)));

        // then
        result.andExpect(jsonPath("$.data.info.totalCourses").value(4))
                .andExpect(jsonPath("$.data.info.totalDistance").value("1,004.2km"))
                .andExpect(jsonPath("$.data.currentCourse.name").value(scale2.name()))
                .andExpect(jsonPath("$.data.currentCourse.totalDistance").value("1km"))
                .andExpect(jsonPath("$.data.currentCourse.achievedDistance").value("250m"))
                .andExpect(jsonPath("$.data.currentCourse.message").value("대전까지 0.8km 남았어요!"))
                .andExpect(jsonPath("$.data.achievedCourses[0].name").value(scale1.name()))
                .andExpect(jsonPath("$.data.achievedCourses[0].totalDistance").value("200m"))
                .andExpect(jsonPath("$.data.achievedCourses[0].achievedAt").value("2021-01-01"));
    }

    @DisplayName("달성한 코스가 없는 경우, 성취 코스는 빈 리스트를 반환합니다.")
    @Test
    void getAchievements_without_achieved() throws Exception {
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
        ResultActions result = mvc.perform(get("/api/v1/scale/courses").param("memberId", String.valueOf(memberId)));

        // then
        result.andExpect(jsonPath("$.data.achievedCourses").isEmpty())
                .andExpect(jsonPath("$.data.currentCourse.name").value(scale1.name()))
                .andExpect(jsonPath("$.data.currentCourse.totalDistance").value("200m"))
                .andExpect(jsonPath("$.data.currentCourse.achievedDistance").value("50m"))
                .andExpect(jsonPath("$.data.currentCourse.message").value("인천까지 0.2km 남았어요!"));
    }

    @DisplayName("전체 코스를 달성했을 경우, 현재 코스는 지구한바퀴로 리턴합니다.")
    @Test
    void getAchievements_all_achieved() throws Exception {
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
        ResultActions result = mvc.perform(get("/api/v1/scale/courses").param("memberId", String.valueOf(memberId)));

        // then
        result.andExpect(jsonPath("$.data.achievedCourses.length()").value(4))
                .andExpect(jsonPath("$.data.currentCourse.name").value("지구 한바퀴"))
                .andExpect(jsonPath("$.data.currentCourse.totalDistance").value("1,004.2km"))
                .andExpect(jsonPath("$.data.currentCourse.achievedDistance").value("1,004.5km"))
                .andExpect(jsonPath("$.data.currentCourse.message").value("축하합니다! 지구 한바퀴 완주하셨네요!"));
    }
}
