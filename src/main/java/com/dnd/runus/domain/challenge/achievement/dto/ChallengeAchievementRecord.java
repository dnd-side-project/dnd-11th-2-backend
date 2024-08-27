package com.dnd.runus.domain.challenge.achievement.dto;

public record ChallengeAchievementRecord(
        ChallengeAchievement challengeAchievement, boolean hasPercentage, PercentageValues percentageValues) {

    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement, PercentageValues percentageValues) {
        this(challengeAchievement, percentageValues != null, percentageValues);
    }

    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement) {
        this(challengeAchievement, false, null);
    }
}
