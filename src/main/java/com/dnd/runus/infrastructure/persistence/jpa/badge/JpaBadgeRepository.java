package com.dnd.runus.infrastructure.persistence.jpa.badge;

import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBadgeRepository extends JpaRepository<BadgeEntity, Long> {
    List<BadgeEntity> findByTypeAndRequiredValueLessThanEqual(BadgeType badgeType, int requiredValue);
}
