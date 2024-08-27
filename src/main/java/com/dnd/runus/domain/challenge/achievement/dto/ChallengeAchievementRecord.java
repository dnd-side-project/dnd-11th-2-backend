package com.dnd.runus.domain.challenge.achievement.dto;

public record ChallengeAchievementRecord(ChallengeAchievement challengeAchievement, PercentageValues percentageValues) {
    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement) {
        this(challengeAchievement, null);
    }
}
