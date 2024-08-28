package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.GoalResultDto;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordAddResultResponse(
        long runningRecordId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        RunningEmoji emoji,
        @Schema(description = "챌린지 정보, achievementMode가 challenge인 경우에만 값이 존재합니다.")
        ChallengeDto challenge,
        @Schema(description = "목표 결과 정보, achievementMode가 goal인 경우에만 값이 존재합니다.")
        GoalResultDto goal,
        @NotNull
        RunningRecordMetricsDto runningData
) {
    public static RunningRecordAddResultResponse from(RunningRecord runningRecord) {
        return buildResponse(runningRecord, null, null);
    }

    public static RunningRecordAddResultResponse of(RunningRecord runningRecord, ChallengeAchievement achievement) {
        return buildResponse(runningRecord,
                new ChallengeDto(
                        achievement.challenge().challengeId(),
                        achievement.challenge().name(),
                        achievement.description(),
                        achievement.challenge().imageUrl(),
                        achievement.isSuccess()
                ),
                null
        );
    }

    public static RunningRecordAddResultResponse of(RunningRecord runningRecord, GoalAchievement achievement) {
        return buildResponse(runningRecord,
                null,
                new GoalResultDto(
                        achievement.getTitle(),
                        achievement.getDescription(),
                        achievement.isAchieved()
                )
        );
    }

    private static RunningRecordAddResultResponse buildResponse(RunningRecord runningRecord, ChallengeDto challenge, GoalResultDto goal) {
        return new RunningRecordAddResultResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                challenge,
                goal,
                new RunningRecordMetricsDto(
                        runningRecord.averagePace(),
                        runningRecord.duration(),
                        runningRecord.distanceMeter(),
                        runningRecord.calorie()
                )
        );
    }
}
