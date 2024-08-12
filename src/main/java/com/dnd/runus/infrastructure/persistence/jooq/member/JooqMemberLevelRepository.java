package com.dnd.runus.infrastructure.persistence.jooq.member;

import com.dnd.runus.domain.member.MemberLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import static com.dnd.runus.jooq.Tables.LEVEL;
import static com.dnd.runus.jooq.Tables.MEMBER_LEVEL;

@Repository
@RequiredArgsConstructor
public class JooqMemberLevelRepository {
    private final DSLContext dsl;

    public MemberLevel.Summary updateMemberLevel(long memberId, int exp) {
        Field<Integer> newExp = MEMBER_LEVEL.EXP.add(exp);

        return dsl.update(MEMBER_LEVEL)
                .set(MEMBER_LEVEL.EXP, newExp)
                .set(
                        MEMBER_LEVEL.LEVEL_ID,
                        dsl.select(LEVEL.ID)
                                .from(LEVEL)
                                .where(newExp.between(LEVEL.EXP_RANGE_START, LEVEL.EXP_RANGE_END))
                                .limit(1))
                .where(MEMBER_LEVEL.MEMBER_ID.eq(memberId))
                .returningResult(MEMBER_LEVEL.ID, MEMBER_LEVEL.MEMBER_ID, MEMBER_LEVEL.LEVEL_ID, MEMBER_LEVEL.EXP)
                .fetchOne(new MemberLevelSummaryMapper());
    }

    private static class MemberLevelSummaryMapper implements RecordMapper<Record, MemberLevel.Summary> {
        @Override
        public MemberLevel.Summary map(Record record) {
            return new MemberLevel.Summary(
                    record.get(MEMBER_LEVEL.ID, long.class),
                    record.get(MEMBER_LEVEL.LEVEL_ID, long.class),
                    record.get(MEMBER_LEVEL.EXP, int.class));
        }
    }
}
