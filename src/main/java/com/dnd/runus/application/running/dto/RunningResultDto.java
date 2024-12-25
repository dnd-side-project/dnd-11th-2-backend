package com.dnd.runus.application.running.dto;


import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.running.RunningRecord;

public record RunningResultDto(
    RunningRecord runningRecord,
    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode runningAchievementMode,
    ChallengeAchievement challengeAchievement,
    GoalAchievement goalAchievement,
    Double percentage
) {
    public static RunningResultDto from(RunningRecord runningRecord) {
        return new RunningResultDto(
            runningRecord,
            com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.NORMAL,
            null,
            null,
            null
        );
    }

    public static RunningResultDto of(RunningRecord runningRecord,
        ChallengeAchievement challengeAchievement, Double percentage) {
        return new RunningResultDto(
            runningRecord,
            com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE,
            challengeAchievement,
            null,
            percentage
        );
    }

    public static RunningResultDto of(RunningRecord runningRecord,
        GoalAchievement goalAchievement, Double percentage) {
        return new RunningResultDto(
            runningRecord,
            com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.GOAL,
            null,
            goalAchievement,
            percentage
        );
    }
}
