package com.dnd.runus.application.scale;

import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse;
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
        ScaleCoursesResponse response = scaleService.getAchievements(memberId);

        // then
        assertNotNull(response);
        assertTrue(response.achievedCourses().isEmpty());
        assertEquals(scale1.name(), response.currentCourse().name());
        assertEquals(runningMeterSum + "m", response.currentCourse().achievedDistance());
    }

    @DisplayName("달성한 코스가 있는 경우, 달성한 코스, 현재 진행중인 코스 정보를 반환한다.")
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
        ScaleCoursesResponse response = scaleService.getAchievements(memberId);

        // then
        assertNotNull(response);
        assertEquals(1, response.achievedCourses().size());

        assertEquals(scale1.name(), response.achievedCourses().getFirst().name());
        assertEquals("0.2km", response.achievedCourses().getFirst().totalDistance());

        assertEquals(scale2.name(), response.currentCourse().name());
        assertEquals("800m", response.currentCourse().achievedDistance());
    }
}
