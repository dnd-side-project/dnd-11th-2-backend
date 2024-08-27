package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievement;

import java.util.Optional;

public interface ChallengeAchievementRepository {
    ChallengeAchievement save(ChallengeAchievement challengeAchievement);

    Optional<ChallengeAchievement> findByRunningId(Long runningId);
}
