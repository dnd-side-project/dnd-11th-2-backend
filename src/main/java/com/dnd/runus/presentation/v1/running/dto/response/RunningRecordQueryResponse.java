package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.application.running.dto.RunningResultDto;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.GoalResultDto;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordQueryResponse(
        long runningRecordId,
        @Schema(description = "러닝 시작 시간")
        LocalDateTime startAt,
        @Schema(description = "러닝 종료 시간")
        LocalDateTime endAt,
        @NotNull
        @Schema(description = "감정 표현, very-good: 최고, good: 좋음, soso: 보통, bad: 나쁨, very-bad: 최악")
        RunningEmoji emotion,
        @NotNull
        @Schema(description = "달성 모드, normal: 일반(목표 설정 X), challenge: 챌린지, goal: 목표")
        RunningAchievementMode achievementMode,
        @Schema(description = "챌린지 정보, achievementMode가 challenge인 경우에만 값이 존재합니다.")
        ChallengeDto challenge,
        @Schema(description = "목표 결과 정보, achievementMode가 goal인 경우에만 값이 존재합니다.")
        GoalResultDto goal,
        @NotNull
        RunningRecordMetricsDto runningData
) {

    public static RunningRecordQueryResponse from(RunningResultDto runningResult) {
        return buildResponse(
            runningResult.runningRecord(),
            runningResult.challengeAchievement() == null ? null
                : ChallengeDto.from(runningResult.challengeAchievement()),
            runningResult.goalAchievement() == null ? null
                : GoalResultDto.from(runningResult.goalAchievement()),
            runningResult.runningAchievementMode()
        );
    }


    private static RunningRecordQueryResponse buildResponse(RunningRecord runningRecord, ChallengeDto challenge, GoalResultDto goal, RunningAchievementMode achievementMode) {
        return new RunningRecordQueryResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                achievementMode,
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
