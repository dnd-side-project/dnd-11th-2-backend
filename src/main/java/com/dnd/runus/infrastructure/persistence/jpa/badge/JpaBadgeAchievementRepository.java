package com.dnd.runus.infrastructure.persistence.jpa.badge;

import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBadgeAchievementRepository extends JpaRepository<BadgeAchievementEntity, Long> {

    void deleteByMemberId(long memberId);
}
