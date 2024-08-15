package com.dnd.runus.infrastructure.persistence.jooq.member;

import com.dnd.runus.domain.level.Level;
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

    public void updateMemberLevel(long memberId, int plusExp) {
        Field<Integer> newExp = MEMBER_LEVEL.EXP.add(plusExp);

        dsl.update(MEMBER_LEVEL)
                .set(MEMBER_LEVEL.EXP, newExp)
                .set(
                        MEMBER_LEVEL.LEVEL_ID,
                        dsl.select(LEVEL.ID)
                                .from(LEVEL)
                                .where(newExp.between(LEVEL.EXP_RANGE_START, LEVEL.EXP_RANGE_END))
                                .limit(1))
                .where(MEMBER_LEVEL.MEMBER_ID.eq(memberId))
                .execute();
    }

    public MemberLevel.Current findByMemberIdWithLevel(long memberId) {
        return dsl.select()
                .from(MEMBER_LEVEL)
                .leftJoin(LEVEL)
                .on(MEMBER_LEVEL.LEVEL_ID.eq(LEVEL.ID))
                .where(MEMBER_LEVEL.MEMBER_ID.eq(memberId))
                .fetchOne(new MemberLevelWithLevelMapper());
    }

    private static class MemberLevelWithLevelMapper implements RecordMapper<Record, MemberLevel.Current> {
        @Override
        public MemberLevel.Current map(Record record) {
            return new MemberLevel.Current(
                    new Level(
                            record.get(LEVEL.ID, long.class),
                            record.get(LEVEL.EXP_RANGE_START, int.class),
                            record.get(LEVEL.EXP_RANGE_END, int.class),
                            record.get(LEVEL.IMAGE_URL, String.class)),
                    record.get(MEMBER_LEVEL.EXP, int.class));
        }
    }
}
