package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.infrastructure.persistence.jpa.badge.JpaBadgeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepository {
    private final JpaBadgeRepository jpaBadgeRepository;

    @Override
    public List<Badge> findByTypeAndRequiredValueLessThanEqual(BadgeType badgeType, int requiredValue) {
        return jpaBadgeRepository.findByTypeAndRequiredValueLessThanEqual(badgeType, requiredValue).stream()
                .map(BadgeEntity::toDomain)
                .toList();
    }
}
