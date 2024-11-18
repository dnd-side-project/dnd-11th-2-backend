package com.dnd.runus.application.scale;

import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
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
class ScaleServiceTest {

    @InjectMocks
    private ScaleService scaleService;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ScaleAchievementRepository scaleAchievementRepository;

    private long memberId;
    private Scale scale1;
    private Scale scale2;
    private Scale scale3;

    @BeforeEach
    void setUp() {
        memberId = 1;
        scale1 = new Scale(1, "서울에서 인천", 200, 1, "서울", "인천");
        scale2 = new Scale(2, "인천에서 대전", 1000, 2, "인천", "대전");
        scale3 = new Scale(3, "대전에서 대구", 3000, 3, "대전", "대구");
    }

    @DisplayName("달성한 코스가 없는 경우, 달성한 코스는 빈 리스트를 반환한다. 현재 진행중인 코스의 달성 거리는 지금까지 달린 거리와 동일해야 한다")
    @Test
    void getAchievements() {
        // given
        given(scaleAchievementRepository.findScaleAchievementLogs(memberId))
                .willReturn(List.of(
                        new ScaleAchievementLog(scale1, null),
                        new ScaleAchievementLog(scale2, null),
                        new ScaleAchievementLog(scale3, null)));
        int runningMeterSum = 50;
        given(runningRecordRepository.findTotalDistanceMeterByMemberId(memberId))
                .willReturn(runningMeterSum);

        // when
        CoursesDto response = scaleService.getAchievements(memberId);

        // then
        assertNotNull(response);
        assertTrue(response.achievedCourses().isEmpty());
        assertEquals(scale1.name(), response.currentCourse().name());
        assertEquals(runningMeterSum, response.currentCourse().achievedMeter());
        assertEquals(runningMeterSum, response.myTotalRunningMeter());
    }

    @DisplayName("달성한 코스가 있는 경우, 달성한 코스, 현재 진행중인 코스 정보를 반환한다." + "각 코스의 달성 미터는 이전 코스들의 달성 미터 + 현재 코스의 달성 미터 이다.")
    @Test
    void getAchievementsWithAchievedCourse() {
        // given
        given(scaleAchievementRepository.findScaleAchievementLogs(memberId))
                .willReturn(List.of(
                        new ScaleAchievementLog(scale1, OffsetDateTime.now()),
                        new ScaleAchievementLog(scale2, null),
                        new ScaleAchievementLog(scale3, null)));
        given(runningRecordRepository.findTotalDistanceMeterByMemberId(memberId))
                .willReturn(1000);

        // when
        CoursesDto response = scaleService.getAchievements(memberId);

        // then
        assertNotNull(response);
        assertEquals(1, response.achievedCourses().size());

        assertEquals(1000, response.myTotalRunningMeter());
        assertEquals(4200, response.totalCoursesDistanceMeter());
        assertEquals(3, response.totalCoursesCount());

        assertEquals(scale1.name(), response.achievedCourses().getFirst().name());
        assertEquals(200, response.achievedCourses().getFirst().sizeMeter());

        assertEquals(scale2.name(), response.currentCourse().name());
        assertEquals(800, response.currentCourse().achievedMeter());
        assertEquals(1200, response.currentCourse().requiredMeterForAchieve());

        CoursesDto.Course nextCourse = response.nextCourses().getFirst();
        assertEquals(scale3.name(), nextCourse.name());
        assertEquals(0, nextCourse.achievedMeter());
        assertEquals(4200, nextCourse.requiredMeterForAchieve());
    }

    @DisplayName("전체 코스를 달성했을 경우, 현재코스는 null, 다음 코스는 빈 리스트를 리턴한다.")
    @Test
    void getAchievements_AllAchieved() {
        // given
        given(scaleAchievementRepository.findScaleAchievementLogs(memberId))
                .willReturn(List.of(
                        new ScaleAchievementLog(scale1, OffsetDateTime.now()),
                        new ScaleAchievementLog(scale2, OffsetDateTime.now()),
                        new ScaleAchievementLog(scale3, OffsetDateTime.now())));
        given(runningRecordRepository.findTotalDistanceMeterByMemberId(memberId))
                .willReturn(4900);

        // when
        CoursesDto response = scaleService.getAchievements(memberId);

        // then
        assertNotNull(response);
        assertNotNull(response.achievedCourses());
        assertNull(response.currentCourse());
        assertNotNull(response.nextCourses());
        assertTrue(response.nextCourses().isEmpty());

        assertEquals(4900, response.myTotalRunningMeter());
        assertEquals(4200, response.totalCoursesDistanceMeter());
        assertEquals(3, response.totalCoursesCount());
        assertEquals(3, response.achievedCourses().size());

        CoursesDto.Course achievedFirstCourses = response.achievedCourses().getFirst();
        assertEquals(scale1.name(), achievedFirstCourses.name());
        assertEquals(200, achievedFirstCourses.achievedMeter());
        assertEquals(200, achievedFirstCourses.sizeMeter());
        assertNotNull(achievedFirstCourses.achievedAt());
    }
}
