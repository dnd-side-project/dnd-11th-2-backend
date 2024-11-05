package com.dnd.runus.application.scale;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.*;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

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
        int memberRunMeterSum =
                runningRecordRepository.findTotalDistanceMeterByMemberIdWithRangeDate(memberId, start, now);

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

        double remainingKm =
                (currentScale.scale().sizeMeter() + achievedCourseMeterSum - memberRunMeterSum) / METERS_IN_A_KILOMETER;

        String message = String.format("%s까지 %.1fkm 남았어요!", currentScale.scale().endName(), remainingKm);

        return new ScaleCoursesResponse.CurrentCourse(
                currentScale.scale().name(),
                currentScale.scale().sizeMeter(),
                memberRunMeterSum - achievedCourseMeterSum,
                message);
    }
}
