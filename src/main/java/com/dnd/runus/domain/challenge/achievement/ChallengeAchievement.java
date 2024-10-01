package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.running.RunningRecord;

import static com.dnd.runus.global.constant.RunningResultComment.FAILURE;
import static com.dnd.runus.global.constant.RunningResultComment.SUCCESS;

public record ChallengeAchievement(
        long challengeAchievementId, Challenge challenge, RunningRecord runningRecord, boolean isSuccess) {
    public ChallengeAchievement(Challenge challenge, RunningRecord runningRecord, boolean isSuccess) {
        this(0, challenge, runningRecord, isSuccess);
    }

    public String description() {
        return isSuccess ? SUCCESS.getComment() : FAILURE.getComment();
    }

    public record Status(long challengeAchievementId, Challenge challenge, boolean isSuccess) {}
}
