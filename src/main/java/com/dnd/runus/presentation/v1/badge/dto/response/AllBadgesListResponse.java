package com.dnd.runus.presentation.v1.badge.dto.response;

import com.dnd.runus.global.constant.BadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public record AllBadgesListResponse(
    List<BadgeWithType> badgeList
) {
    public record BadgeWithType(
        @Schema(description = "뱃지 카테고리 이름")
        String category,
        @Schema(description = "뱃지 리스트")
        List<AchievedBadge> badges
    ) {
    }

    public static AllBadgesListResponse from(List<AchievedBadge> recencyBadges, EnumMap<BadgeType, List<AchievedBadge>> allBadges) {
        List<BadgeWithType> response = new ArrayList<>();
        response.add(new BadgeWithType("신규 뱃지", recencyBadges));

        EnumSet<BadgeType> badgeTypes = EnumSet.allOf(BadgeType.class);
        badgeTypes.forEach(type -> response.add(
            new BadgeWithType(
                type.getName(),
                allBadges.getOrDefault(type, Collections.emptyList())
            )
        ));

        return new AllBadgesListResponse(response);
    }
}
