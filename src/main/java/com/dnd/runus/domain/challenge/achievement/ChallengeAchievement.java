package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.running.RunningRecord;

public record ChallengeAchievement(
        long ChallengeAchievementId, Challenge challenge, RunningRecord runningRecord, boolean isSuccess) {
    public ChallengeAchievement(Challenge challenge, RunningRecord runningRecord, boolean isSuccess) {
        this(0, challenge, runningRecord, isSuccess);
    }
}
