package com.dnd.runus.presentation.v1.badge.dto.response;

import java.util.List;

public record AchievedBadgesResponse(
        List<AchievedBadge> badges
) {
}
