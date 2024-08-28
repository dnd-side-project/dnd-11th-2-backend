package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordAddResultResponse(
        long runningRecordId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        RunningEmoji emoji,
        @Schema(description = "챌린지 정보, 챌린지를 하지 않은 경우 null입니다.")
        ChallengeDto challenge,
        @NotNull
        RunningRecordMetricsDto runningData
) {
    public static RunningRecordAddResultResponse from(RunningRecord runningRecord) {
        return new RunningRecordAddResultResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                null,
                new RunningRecordMetricsDto(
                        runningRecord.averagePace(),
                        runningRecord.duration(),
                        runningRecord.distanceMeter(),
                        runningRecord.calorie()
                ));
    }

    public static RunningRecordAddResultResponse of(RunningRecord runningRecord, ChallengeAchievement achievement) {
        return new RunningRecordAddResultResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                new ChallengeDto(
                        achievement.challenge().challengeId(),
                        achievement.challenge().name(),
                        achievement.challenge().formatExpectedTime(),
                        achievement.challenge().imageUrl()
                ),
                new RunningRecordMetricsDto(
                        runningRecord.averagePace(),
                        runningRecord.duration(),
                        runningRecord.distanceMeter(),
                        runningRecord.calorie()
                ));
    }
}
