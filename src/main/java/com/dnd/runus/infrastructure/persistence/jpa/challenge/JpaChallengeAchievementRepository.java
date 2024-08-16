package com.dnd.runus.infrastructure.persistence.jpa.challenge;

import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChallengeAchievementRepository extends JpaRepository<ChallengeAchievementEntity, Long> {}
