package com.dnd.runus.application.running;

import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.presentation.v2.running.dto.response.RunningRecordMonthlySummaryResponseV2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

@Service
public class RunningRecordServiceV2 {

    private final RunningRecordRepository runningRecordRepository;
    private final ScaleAchievementRepository scaleAchievementRepository;

    private static final DecimalFormat KILO_METER_UNDER_1_POINT_FORMATTER = new DecimalFormat("#,###.#km");

    public RunningRecordServiceV2(
            RunningRecordRepository runningRecordRepository, ScaleAchievementRepository scaleAchievementRepository) {
        this.runningRecordRepository = runningRecordRepository;
        this.scaleAchievementRepository = scaleAchievementRepository;
    }

    // todo 지구 한바퀴 작업 후 리팩터링 예정
    @Transactional(readOnly = true)
    public RunningRecordMonthlySummaryResponseV2.PercentageBoxWithMessage getPercentageValues(long memberId) {
        // 지구한바퀴 코스 조회
        List<ScaleAchievementLog> scaleAchievementLogs = scaleAchievementRepository.findScaleAchievementLogs(memberId);
        ScaleAchievementLog currentCourseLog = scaleAchievementLogs.stream()
                .filter(log -> log.achievedDate() == null)
                .findFirst()
                .orElse(null);

        // todo 정해지면 하드코드 수정
        if (currentCourseLog == null) {
            return RunningRecordMonthlySummaryResponseV2.PercentageBoxWithMessage.builder()
                    .message("축하합니다! 지구 한바퀴 완주하셨네요!")
                    .percentageBox(RunningRecordMonthlySummaryResponseV2.PercentageBox.builder()
                            .startName("한국")
                            .endName("지구 한바퀴")
                            .percentage(100)
                            .build())
                    .build();
        }

        // 사용자가 달린 전체 거리 확인
        int totalRunningDistanceMeter = runningRecordRepository.findTotalDistanceMeterByMemberId(memberId);
        // 성취한 코스의 전체 합 거리
        int achievedCourseMeterSum = scaleAchievementLogs.stream()
                .filter(log -> log.achievedDate() != null)
                .mapToInt(log -> log.scale().sizeMeter())
                .sum();

        // 현재 코스에서 사용자가 성취한 거리 합
        double currentCourseAchievedKmSum =
                (totalRunningDistanceMeter - achievedCourseMeterSum) / METERS_IN_A_KILOMETER;
        // 현재 코스 완주를 위해 필요한 키로미터 값
        double courseSizeKm = currentCourseLog.scale().sizeMeter() / METERS_IN_A_KILOMETER;

        double percentage = calPercentage(courseSizeKm, currentCourseAchievedKmSum);

        return RunningRecordMonthlySummaryResponseV2.PercentageBoxWithMessage.builder()
                .message(String.format(
                        "%s까지 %s 남았어요!",
                        currentCourseLog.scale().endName(),
                        KILO_METER_UNDER_1_POINT_FORMATTER.format(courseSizeKm - currentCourseAchievedKmSum)))
                .percentageBox(RunningRecordMonthlySummaryResponseV2.PercentageBox.builder()
                        .startName(currentCourseLog.scale().startName())
                        .endName(currentCourseLog.scale().endName())
                        .percentage(percentage)
                        .build())
                .build();
    }

    private double calPercentage(double totalRangeValue, double currentValue) {
        if (totalRangeValue <= 0) return 0;
        return currentValue / totalRangeValue;
    }
}
