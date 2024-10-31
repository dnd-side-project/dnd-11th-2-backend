package com.dnd.runus.presentation.v1.badge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AllBadgesListResponse(
    @Schema(description = "최신 뱃지 목록(일주일 기준), 없으면 빈리스트 리턴")
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
