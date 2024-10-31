package com.dnd.runus.domain.badge;

import com.dnd.runus.global.constant.BadgeType;

import java.util.List;

public interface BadgeRepository {
    List<Badge> findByTypeAndRequiredValueLessThanEqual(BadgeType badgeType, int requiredValue);
}
