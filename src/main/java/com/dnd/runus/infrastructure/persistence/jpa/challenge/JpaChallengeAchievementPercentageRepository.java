package com.dnd.runus.infrastructure.persistence.jpa.challenge;

import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementPercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaChallengeAchievementPercentageRepository
        extends JpaRepository<ChallengeAchievementPercentageEntity, Long> {
    void deleteAllByChallengeAchievementIdIn(List<Long> ids);
}
