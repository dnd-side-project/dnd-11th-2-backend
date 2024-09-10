package com.dnd.runus.domain.challenge.achievement;

import java.util.List;

public interface ChallengeAchievementPercentageRepository {
    PercentageValues save(ChallengeAchievementRecord challengeAchievementRecord);

    void deleteByChallengeAchievementIds(List<Long> challengeAchievementIds);
}
