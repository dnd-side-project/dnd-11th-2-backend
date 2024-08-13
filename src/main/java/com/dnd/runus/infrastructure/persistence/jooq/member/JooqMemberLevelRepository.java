package com.dnd.runus.infrastructure.persistence.jooq.member;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
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
}
