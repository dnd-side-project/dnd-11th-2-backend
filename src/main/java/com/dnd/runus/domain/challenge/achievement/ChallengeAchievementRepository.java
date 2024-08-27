package com.dnd.runus.domain.challenge.achievement;

import java.util.Optional;

public interface ChallengeAchievementRepository {
    ChallengeAchievementRecord.ChallengeAchievement save(
            ChallengeAchievementRecord.ChallengeAchievement challengeAchievement);

    Optional<ChallengeAchievementRecord.ChallengeAchievement> findByRunningId(Long runningId);
}
