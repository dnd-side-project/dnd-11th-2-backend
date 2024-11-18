package com.dnd.runus.application.scale;

import com.dnd.runus.application.scale.dto.CoursesDto;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.domain.scale.ScaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScaleService {

    private final ScaleRepository scaleRepository;
    private final ScaleAchievementRepository scaleAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;

    @Transactional
    public void saveScaleAchievements(Member member) {
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(member.memberId());
        if (achievableScaleIds == null || achievableScaleIds.isEmpty() || achievableScaleIds.getFirst() == null) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<ScaleAchievement> scaleAchievements = achievableScaleIds.stream()
                .map(id -> new ScaleAchievement(member, new Scale(id), now))
                .toList();
        scaleAchievementRepository.saveAll(scaleAchievements);
    }

    @Transactional(readOnly = true)
    public CoursesDto getAchievements(long memberId) {
        int totalRunningDistanceMeter = runningRecordRepository.findTotalDistanceMeterByMemberId(memberId);
        // 지구 한바퀴 전체 코스 조회
        List<CoursesDto.Course> courses = convertToCoursesDto(
                scaleAchievementRepository.findScaleAchievementLogs(memberId), totalRunningDistanceMeter);

        List<CoursesDto.Course> achievedCourses =
                courses.stream().filter(course -> course.achievedAt() != null).toList();

        // 완주하지 못한 코스(현재 + 다음 코스)
        List<CoursesDto.Course> notAchievedCourses =
                courses.stream().filter(course -> course.achievedAt() == null).collect(Collectors.toList());

        CoursesDto.Course currentCourse = null;
        CoursesDto.Course lastCourse;

        if (notAchievedCourses.isEmpty()) {
            // 전체 코스를 완주했을 경우, 현재 코스에
            lastCourse = achievedCourses.getLast();
        } else {
            currentCourse = notAchievedCourses.getFirst();
            lastCourse = notAchievedCourses.getLast();

            // nextCourses 값을 구하기 위해 현재 코스 삭제
            notAchievedCourses.removeFirst();
        }

        return CoursesDto.builder()
                .totalCoursesCount(lastCourse.order())
                .totalCoursesDistanceMeter(lastCourse.requiredMeterForAchieve())
                .myTotalRunningMeter(totalRunningDistanceMeter)
                .achievedCourses(achievedCourses)
                .currentCourse(currentCourse)
                .nextCourses(notAchievedCourses)
                .build();
    }

    private List<CoursesDto.Course> convertToCoursesDto(
            List<ScaleAchievementLog> achievementLogs, int totalRunningDistanceMeter) {

        List<CoursesDto.Course> result = new ArrayList<>();
        int requiredForAchieveSum = 0;

        for (ScaleAchievementLog achievementLog : achievementLogs) {
            // 해당 코스에서 사용자가 달성한 미터값 계산(아직 달성 못한 코스는 0으로, 달성한 코스는 sizeMeter로 들어감)
            int achievedMeter = Math.min(
                    Math.max(0, totalRunningDistanceMeter - requiredForAchieveSum),
                    achievementLog.scale().sizeMeter());
            // 해당 코스에서 달성하기 위해 필요한 전체 미터값 계산
            requiredForAchieveSum += achievementLog.scale().sizeMeter();

            result.add(CoursesDto.Course.of(achievementLog, requiredForAchieveSum, achievedMeter));
        }

        return result;
    }
}
