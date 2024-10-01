package com.dnd.runus.infrastructure.persistence.jooq.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import com.dnd.runus.global.constant.BadgeType;
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
public class JooqBadgeRepository {
    private final DSLContext dsl;

    public List<BadgeWithAchieveStatusAndAchievedAt> findAllBadgesWithAchieveStatusByMemberId(long memberId) {
        return dsl.select(BADGE.ID, BADGE.NAME, BADGE.IMAGE_URL, BADGE.TYPE, BADGE_ACHIEVEMENT.CREATED_AT)
                .from(BADGE)
                .leftJoin(BADGE_ACHIEVEMENT)
                .on(BADGE.ID.eq(BADGE_ACHIEVEMENT.BADGE_ID))
                .and(BADGE_ACHIEVEMENT.MEMBER_ID.eq(memberId))
                .fetch(new AllBadgesMapper());
    }

    private static class AllBadgesMapper implements RecordMapper<Record, BadgeWithAchieveStatusAndAchievedAt> {
        @Override
        public BadgeWithAchieveStatusAndAchievedAt map(Record record) {
            OffsetDateTime achievedTime = record.get(BADGE_ACHIEVEMENT.CREATED_AT);
            return new BadgeWithAchieveStatusAndAchievedAt(
                    new Badge(
                            record.get(BADGE.ID),
                            record.get(BADGE.NAME),
                            record.get(BADGE.IMAGE_URL),
                            BadgeType.valueOf(record.get(BADGE.TYPE))),
                    achievedTime);
        }
    }
}
