package com.dnd.runus.infrastructure.persistence.jpa.badge;

import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaBadgeAchievementRepository extends JpaRepository<BadgeAchievementEntity, Long> {

    @Transactional
    void deleteByMemberId(long memberId);
}
