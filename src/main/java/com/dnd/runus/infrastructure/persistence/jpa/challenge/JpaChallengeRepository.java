package com.dnd.runus.infrastructure.persistence.jpa.challenge;

import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChallengeRepository extends JpaRepository<ChallengeEntity, Long> {}
