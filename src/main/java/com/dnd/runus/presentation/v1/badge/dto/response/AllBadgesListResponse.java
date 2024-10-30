package com.dnd.runus.presentation.v1.badge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AllBadgesListResponse(
    List<AchievedBadge> recencyBadges,
    List<BadgesWithType> badgesList
) {
    public record BadgesWithType(
        @Schema(description = "뱃지 카테고리 이름")
        String category,
        @Schema(description = "뱃지 리스트")
        List<AchievedBadge> badges
    ) {
    }
}
