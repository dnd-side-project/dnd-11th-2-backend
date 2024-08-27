package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.dto.PercentageValues;

public interface ChallengeAchievementPercentageRepository {
    PercentageValues save(ChallengeAchievementRecord challengeAchievementRecord);
}
