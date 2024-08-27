package com.dnd.runus.domain.challenge.achievement;

public record ChallengeAchievementRecord(ChallengeAchievement challengeAchievement, PercentageValues percentageValues) {
    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement) {
        this(challengeAchievement, null);
    }
}
