package com.dnd.runus.presentation.v1.badge.dto.response;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record AllBadgesListResponse(
    @Schema(description = "신규 뱃지 목록")
    List<BadgeWithAchievedStatus> recencyBadges,
    @Schema(description = "개인기록 뱃지 목록")
    List<BadgeWithAchievedStatus> personalBadges,
    @Schema(description = "러닝 거리 뱃지 목록")
    List<BadgeWithAchievedStatus> distanceBadges,
    @Schema(description = "연속 뱃지 목록")
    List<BadgeWithAchievedStatus> streakBadges,
    @Schema(description = "사간 뱃지 목록")
    List<BadgeWithAchievedStatus> durationBadges,
    @Schema(description = "레벨 뱃지 목록")
    List<BadgeWithAchievedStatus> levelBadges
) {
    public record BadgeWithAchievedStatus(
        @Schema(description = "뱃지 id")
        long badgeId,
        @Schema(description = "뱃지 이름")
        String name,
        @Schema(description = "뱃지 이미지 url")
        String imageUrl,
        @Schema(description = "뱃지 달성 여부")
        boolean isAchieved,
        @Schema(description = "배지 달성 날짜")
        LocalDateTime achievedAt
    ) {
        public static BadgeWithAchievedStatus from(
            BadgeWithAchieveStatusAndAchievedAt badgeWithAchievedStatus) {
            Badge badge = badgeWithAchievedStatus.badge();
            return new BadgeWithAchievedStatus(badge.badgeId(), badge.name(), badge.imageUrl(), badgeWithAchievedStatus.isAchieved(), badgeWithAchievedStatus.achievedAt());
        }
    }
}
