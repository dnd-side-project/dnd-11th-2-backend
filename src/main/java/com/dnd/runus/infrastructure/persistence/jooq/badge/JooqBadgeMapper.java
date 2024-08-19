package com.dnd.runus.infrastructure.persistence.jooq.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.global.constant.BadgeType;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static com.dnd.runus.jooq.Tables.BADGE;

public class JooqBadgeMapper implements RecordMapper<Record, Badge> {

    @Override
    public Badge map(Record record) {
        return new Badge(
                record.get(BADGE.ID),
                record.get(BADGE.NAME),
                record.get(BADGE.DESCRIPTION),
                record.get(BADGE.IMAGE_URL),
                BadgeType.valueOf(record.get(BADGE.TYPE)),
                record.get(BADGE.REQUIRED_VALUE));
    }
}
