package com.dnd.runus.application.scale;

import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScaleService {

    private final ScaleAchievementRepository scaleAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;

    public ScaleCoursesResponse getAchievements(long memberId) {
        List<ScaleAchievementLog> scaleAchievementLogs = scaleAchievementRepository.findScaleAchievementLogs(memberId);

        ScaleCoursesResponse.Info info = new ScaleCoursesResponse.Info(
                scaleAchievementLogs.size(),
                scaleAchievementLogs.stream()
                        .mapToInt(log -> log.scale().sizeMeter())
                        .sum());

        return new ScaleCoursesResponse(
                info,
                getAchievedCourses(scaleAchievementLogs),
                calculateCurrentScaleLeftMeter(scaleAchievementLogs, memberId));
    }

    private List<ScaleCoursesResponse.AchievedCourse> getAchievedCourses(
            List<ScaleAchievementLog> scaleAchievementLogs) {
        boolean hasAchievedCourse = scaleAchievementLogs.stream().anyMatch(log -> log.achievedDate() != null);
        if (!hasAchievedCourse) {
            return List.of();
        }

        return scaleAchievementLogs.stream()
                .filter(log -> log.achievedDate() != null)
                .map(log -> new ScaleCoursesResponse.AchievedCourse(
                        log.scale().name(),
                        log.scale().sizeMeter(),
                        log.achievedDate().toLocalDate()))
                .toList();
    }

    private ScaleCoursesResponse.CurrentCourse calculateCurrentScaleLeftMeter(
            List<ScaleAchievementLog> scaleAchievementLogs, long memberId) {

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = OffsetDateTime.of(LocalDate.of(1, 1, 1).atStartOfDay(), now.getOffset());
        int memberRunMeterSum = runningRecordRepository.findTotalDistanceMeterByMemberId(memberId, start, now);

        ScaleAchievementLog currentScale = scaleAchievementLogs.stream()
                .filter(log -> log.achievedDate() == null)
                .findFirst()
                .orElse(null);

        if (currentScale == null) {
            return new ScaleCoursesResponse.CurrentCourse("지구 한바퀴", 0, 0, "축하합니다! 지구 한바퀴 완주하셨네요!");
        }

        int achievedCourseMeterSum = scaleAchievementLogs.stream()
                .filter(log -> log.achievedDate() != null)
                .mapToInt(log -> log.scale().sizeMeter())
                .sum();

        double remainingKm = (currentScale.scale().sizeMeter() + achievedCourseMeterSum - memberRunMeterSum) / 1000.0;

        String message = String.format("%s까지 %.1fkm 남았어요!", currentScale.scale().endName(), remainingKm);

        return new ScaleCoursesResponse.CurrentCourse(
                currentScale.scale().name(),
                currentScale.scale().sizeMeter(),
                memberRunMeterSum - achievedCourseMeterSum,
                message);
    }
}
