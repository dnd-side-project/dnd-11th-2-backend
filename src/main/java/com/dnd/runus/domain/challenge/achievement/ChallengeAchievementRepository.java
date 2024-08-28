package com.dnd.runus.domain.challenge.achievement;

import java.util.Optional;

public interface ChallengeAchievementRepository {

    ChallengeAchievement save(ChallengeAchievement challengeAchievement);

    Optional<ChallengeAchievement> findByRunningId(Long runningId);
}
