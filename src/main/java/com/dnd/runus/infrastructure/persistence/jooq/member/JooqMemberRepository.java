package com.dnd.runus.infrastructure.persistence.jooq.member;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.dnd.runus.jooq.Tables.MEMBER;

@Repository
@RequiredArgsConstructor
public class JooqMemberRepository {

    private final DSLContext dsl;

    public void updateMemberNickname(long memberId, String nickname) {
        dsl.update(MEMBER)
                .set(MEMBER.NICKNAME, nickname)
                .where(MEMBER.ID.eq(memberId))
                .execute();
    }
}
