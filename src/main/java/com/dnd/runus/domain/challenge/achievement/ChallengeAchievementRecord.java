package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.running.RunningRecord;

public record ChallengeAchievementRecord(
        ChallengeAchievement challengeAchievement, boolean hasPercentage, PercentageValues percentageValues) {

    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement, PercentageValues percentageValues) {
        this(challengeAchievement, percentageValues != null, percentageValues);
    }

    public ChallengeAchievementRecord(ChallengeAchievement challengeAchievement) {
        this(challengeAchievement, false, null);
    }

    public record ChallengeAchievement(
            Long ChallengeAchievementId, long challengeId, RunningRecord runningRecord, boolean isSuccess) {
        public ChallengeAchievement(long challengeId, RunningRecord runningRecord, boolean isSuccess) {
            this(null, challengeId, runningRecord, isSuccess);
        }
    }

    public record PercentageValues(int achievementValue, int startValue, int endValue) {
        // todo 나중에 조회 기능 생기면 사용
        private static int calPercentage(int achievementValue, int startValue, int endValue) {
            int percent = (int) Math.floor(((double) (achievementValue) / (endValue - startValue)) * 100);
            return Math.min(percent, 100);
        }
    }
}
