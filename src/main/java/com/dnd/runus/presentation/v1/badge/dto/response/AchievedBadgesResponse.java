package com.dnd.runus.presentation.v1.badge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AchievedBadgesResponse(
        List<AchievedBadge> badges
) {
    public record AchievedBadge(
            long badgeId,
            String name,
            String imageUrl,
            LocalDateTime achievedAt
    ) {
    }
}
