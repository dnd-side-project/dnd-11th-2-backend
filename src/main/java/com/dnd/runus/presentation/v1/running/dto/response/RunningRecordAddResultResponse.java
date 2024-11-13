package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.GoalResultDto;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordAddResultResponse(
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
    public static RunningRecordAddResultResponse from(RunningRecord runningRecord) {
        return buildResponse(runningRecord, null, null, RunningAchievementMode.NORMAL);
    }

    public static RunningRecordAddResultResponse of(RunningRecord runningRecord, ChallengeAchievement achievement) {
        return buildResponse(runningRecord,
                new ChallengeDto(
                        achievement.challenge().challengeId(),
                        achievement.isSuccess() ? achievement.challenge().name() + " 성공!" : achievement.challenge().name() + " 실패",
                        achievement.description(),
                        achievement.challenge().imageUrl(),
                        achievement.isSuccess()
                ),
                null,
                RunningAchievementMode.CHALLENGE
        );
    }

    public static RunningRecordAddResultResponse of(RunningRecord runningRecord, GoalAchievement achievement) {
        return buildResponse(runningRecord,
                null,
                new GoalResultDto(
                        achievement.getTitle(),
                        achievement.getDescription(),
                        achievement.getIconUrl(),
                        achievement.isAchieved()
                ),
                RunningAchievementMode.GOAL
        );
    }

    private static RunningRecordAddResultResponse buildResponse(RunningRecord runningRecord, ChallengeDto challenge, GoalResultDto goal, RunningAchievementMode achievementMode) {
        return new RunningRecordAddResultResponse(
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
