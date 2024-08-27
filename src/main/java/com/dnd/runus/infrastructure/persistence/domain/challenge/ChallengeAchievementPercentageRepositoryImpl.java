package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeAchievementPercentageRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementPercentageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeAchievementPercentageRepositoryImpl implements ChallengeAchievementPercentageRepository {

    private final JpaChallengeAchievementPercentageRepository jpaPercentageRepository;

    @Override
    public PercentageValues save(ChallengeAchievementRecord record) {
        return jpaPercentageRepository
                .save(ChallengeAchievementPercentageEntity.from(
                        record.percentageValues(), record.challengeAchievement()))
                .toDomain();
    }
}
