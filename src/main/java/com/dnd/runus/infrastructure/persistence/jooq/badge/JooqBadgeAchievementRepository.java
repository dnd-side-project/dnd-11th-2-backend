package com.dnd.runus.infrastructure.persistence.jooq.badge;

import com.dnd.runus.domain.badge.BadgeAchievement;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.jooq.Tables.BADGE;
import static com.dnd.runus.jooq.Tables.BADGE_ACHIEVEMENT;

@Repository
@RequiredArgsConstructor
public class JooqBadgeAchievementRepository {
    private final DSLContext dsl;

    public List<BadgeAchievement.OnlyBadge> findByMemberIdWithBadge(long memberId) {
        return dsl.select()
                .from(BADGE_ACHIEVEMENT)
                .join(BADGE)
                .on(BADGE_ACHIEVEMENT.BADGE_ID.eq(BADGE.ID))
                .where(BADGE_ACHIEVEMENT.MEMBER_ID.eq(memberId))
                .fetch(new BadgeAchievementMapper());
    }

    private static class BadgeAchievementMapper implements RecordMapper<Record, BadgeAchievement.OnlyBadge> {
        @Override
        public BadgeAchievement.OnlyBadge map(Record record) {
            return new BadgeAchievement.OnlyBadge(
                    record.get(BADGE_ACHIEVEMENT.ID),
                    new JooqBadgeMapper().map(record),
                    record.get(BADGE_ACHIEVEMENT.CREATED_AT, OffsetDateTime.class),
                    record.get(BADGE_ACHIEVEMENT.UPDATED_AT, OffsetDateTime.class));
        }
    }
}
