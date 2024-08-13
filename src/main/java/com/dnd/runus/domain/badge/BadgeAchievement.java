package com.dnd.runus.domain.badge;

import com.dnd.runus.domain.member.Member;

import java.time.OffsetDateTime;

public record BadgeAchievement(
        long badgeAchievementId, Badge badge, Member member, OffsetDateTime createdAt, OffsetDateTime updatedAt) {

    public BadgeAchievement(Badge badge, Member member) {
        this(0, badge, member, null, null);
    }
}
