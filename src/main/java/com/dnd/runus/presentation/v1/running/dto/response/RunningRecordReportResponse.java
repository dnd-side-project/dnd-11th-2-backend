package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordReportResponse(
        long runningRecordId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        RunningEmoji emoji,
        String nickname,
        @Schema(description = "챌린지 정보")
        ChallengeDto challenge,
        @NotNull
        RunningRecordMetricsDto runningData
) {
    public static RunningRecordReportResponse from(RunningRecord runningRecord) {
        return new RunningRecordReportResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                runningRecord.member().nickname(),
                new ChallengeDto(-1), // TODO: 챌린지 기능 추가 후 수정
                new RunningRecordMetricsDto(
                        runningRecord.averagePace(),
                        runningRecord.duration(),
                        runningRecord.distanceMeter(),
                        runningRecord.calorie(),
                        runningRecord.route()
                ));
    }
}
