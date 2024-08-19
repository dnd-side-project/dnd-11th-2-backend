package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeAchievementRepositoryImpl implements ChallengeAchievementRepository {

    private final JpaChallengeAchievementRepository challengeAchievementRepository;

    @Override
    public ChallengeAchievement save(ChallengeAchievement challengeAchievement) {
        return challengeAchievementRepository
                .save(ChallengeAchievementEntity.from(challengeAchievement))
                .toDomain();
    }

    @Override
    public Optional<ChallengeAchievement> findByMemberIdAndRunningRecordId(long memberId, long runningId) {
        return challengeAchievementRepository
                .findByMemberIdAndRunningRecordId(memberId, runningId)
                .map(ChallengeAchievementEntity::toDomain);
    }
}
