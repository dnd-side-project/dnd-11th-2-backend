package com.dnd.runus.domain.badge;

import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;

import java.time.OffsetDateTime;

public record BadgeWithAchievedStatusAndRecentlyStatus(
        AllBadgesListResponse.BadgeWithAchievedStatus badgeWithAchievedStatus, BadgeType badgeType, boolean isRecent) {
    public static BadgeWithAchievedStatusAndRecentlyStatus from(
            BadgeWithAchieveStatusAndAchievedAt badgeWithAchievedStatus, OffsetDateTime criterionDate) {
        return new BadgeWithAchievedStatusAndRecentlyStatus(
                AllBadgesListResponse.BadgeWithAchievedStatus.of(
                        badgeWithAchievedStatus.badge(), badgeWithAchievedStatus.isAchieved()),
                badgeWithAchievedStatus.badge().type(),
                badgeWithAchievedStatus.isAchieved() && criterionDate.isBefore(badgeWithAchievedStatus.achievedAt()));
    }
}
