package com.dnd.runus.infrastructure.persistence.jooq.badge;

import com.dnd.runus.domain.badge.BadgeAchievement;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.jooq.Tables.BADGE;
import static com.dnd.runus.jooq.Tables.BADGE_ACHIEVEMENT;

@Repository
@RequiredArgsConstructor
public class JooqBadgeAchievementRepository {
    private final DSLContext dsl;

    public List<BadgeAchievement.OnlyBadge> findByMemberIdWithBadgeOrderByAchievedAtLimit(long memberId, int limit) {
        return dsl.select()
                .from(BADGE_ACHIEVEMENT)
                .join(BADGE)
                .on(BADGE_ACHIEVEMENT.BADGE_ID.eq(BADGE.ID))
                .where(BADGE_ACHIEVEMENT.MEMBER_ID.eq(memberId))
                .orderBy(BADGE_ACHIEVEMENT.CREATED_AT.desc())
                .limit(limit)
                .fetch(badge -> new BadgeAchievement.OnlyBadge(
                        badge.get(BADGE_ACHIEVEMENT.ID),
                        new JooqBadgeMapper().map(badge),
                        badge.get(BADGE_ACHIEVEMENT.CREATED_AT, OffsetDateTime.class),
                        badge.get(BADGE_ACHIEVEMENT.UPDATED_AT, OffsetDateTime.class)));
    }

    public List<BadgeAchievement.OnlyBadge> findByMemberIdOrderByBadgeTypeAndAchievedAt(long memberId) {
        return dsl.select()
                .from(BADGE_ACHIEVEMENT)
                .join(BADGE)
                .on(BADGE_ACHIEVEMENT.BADGE_ID.eq(BADGE.ID))
                .where(BADGE_ACHIEVEMENT.MEMBER_ID.eq(memberId))
                .orderBy(BADGE.TYPE, BADGE_ACHIEVEMENT.CREATED_AT.desc())
                .fetch(badge -> new BadgeAchievement.OnlyBadge(
                        badge.get(BADGE_ACHIEVEMENT.ID),
                        new JooqBadgeMapper().map(badge),
                        badge.get(BADGE_ACHIEVEMENT.CREATED_AT, OffsetDateTime.class),
                        badge.get(BADGE_ACHIEVEMENT.UPDATED_AT, OffsetDateTime.class)));
    }

    public void saveAllIgnoreDuplicated(List<BadgeAchievement> badgeAchievements) {
        OffsetDateTime now = OffsetDateTime.now();
        dsl.batch(badgeAchievements.stream()
                        .map(badgeAchievement -> dsl.insertInto(BADGE_ACHIEVEMENT)
                                .set(
                                        BADGE_ACHIEVEMENT.BADGE_ID,
                                        badgeAchievement.badge().badgeId())
                                .set(
                                        BADGE_ACHIEVEMENT.MEMBER_ID,
                                        badgeAchievement.member().memberId())
                                .set(
                                        BADGE_ACHIEVEMENT.CREATED_AT,
                                        badgeAchievement.createdAt() == null ? now : badgeAchievement.createdAt())
                                .set(
                                        BADGE_ACHIEVEMENT.UPDATED_AT,
                                        badgeAchievement.updatedAt() == null ? now : badgeAchievement.updatedAt())
                                .onConflictDoNothing())
                        .toList())
                .execute();
    }
}
