package com.dnd.runus.infrastructure.persistence.jpa.scale;

import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaScaleAchievementRepository extends JpaRepository<ScaleAchievementEntity, Long> {}
