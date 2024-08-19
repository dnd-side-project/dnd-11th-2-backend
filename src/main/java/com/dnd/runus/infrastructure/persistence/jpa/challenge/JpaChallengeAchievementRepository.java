package com.dnd.runus.infrastructure.persistence.jpa.challenge;

import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaChallengeAchievementRepository extends JpaRepository<ChallengeAchievementEntity, Long> {
    Optional<ChallengeAchievementEntity> findByMemberIdAndRunningRecordId(long memberId, long runningId);
}
