package com.dnd.runus.domain.badge;

import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;

import java.time.OffsetDateTime;

public record BadgeWithAchievedStatusAndRecentlyStatus(
        AllBadgesListResponse.BadgeWithAchievedStatus badgeWithAchievedStatus, BadgeType badgeType, boolean isRecent) {
    public static BadgeWithAchievedStatusAndRecentlyStatus from(
            BadgeWithAchieveStatusAndAchievedAt badgeWithAchievedStatus, OffsetDateTime criterionDate) {
        return new BadgeWithAchievedStatusAndRecentlyStatus(
                new AllBadgesListResponse.BadgeWithAchievedStatus(
                        badgeWithAchievedStatus.badge().badgeId(),
                        badgeWithAchievedStatus.badge().name(),
                        badgeWithAchievedStatus.badge().imageUrl(),
                        badgeWithAchievedStatus.isAchieved()),
                badgeWithAchievedStatus.badge().type(),
                badgeWithAchievedStatus.achievedAt() != null
                        && criterionDate.isBefore(badgeWithAchievedStatus.achievedAt()));
    }
}
