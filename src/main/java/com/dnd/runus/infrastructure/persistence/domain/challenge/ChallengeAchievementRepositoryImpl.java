package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeAchievementRepositoryImpl implements ChallengeAchievementRepository {

    private final JpaChallengeAchievementRepository jpaChallengeAchievementRepository;
    private final JooqChallengeRepository jooqChallengeRepository;

    @Override
    public ChallengeAchievement save(ChallengeAchievement challengeAchievement) {
        return jpaChallengeAchievementRepository
                .save(ChallengeAchievementEntity.from(challengeAchievement))
                .toDomain(challengeAchievement.challenge());
    }

    @Override
    public Optional<ChallengeAchievement> findByRunningId(Long runningId) {
        return jpaChallengeAchievementRepository
                .findByRunningRecordId(runningId)
                .map(challengeAchievementEntity -> challengeAchievementEntity.toDomain(null));
    }
}
