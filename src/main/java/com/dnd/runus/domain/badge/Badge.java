package com.dnd.runus.domain.badge;

import com.dnd.runus.global.constant.BadgeType;

public record Badge(
        long badgeId, String name, String description, String imageUrl, BadgeType type, int requiredValue) {}
