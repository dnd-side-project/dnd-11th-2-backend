package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.running.RunningRecord;

public record ChallengeAchievement(
        Long ChallengeAchievementId, long challengeId, RunningRecord runningRecord, boolean isSuccess) {
    public ChallengeAchievement(long challengeId, RunningRecord runningRecord, boolean isSuccess) {
        this(null, challengeId, runningRecord, isSuccess);
    }
}
