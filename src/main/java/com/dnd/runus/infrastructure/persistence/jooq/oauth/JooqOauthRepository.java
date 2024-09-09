package com.dnd.runus.infrastructure.persistence.jooq.oauth;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.dnd.runus.jooq.Tables.CHALLENGE_ACHIEVEMENT;
import static com.dnd.runus.jooq.Tables.CHALLENGE_ACHIEVEMENT_PERCENTAGE;
import static com.dnd.runus.jooq.Tables.GOAL_ACHIEVEMENT;
import static com.dnd.runus.jooq.Tables.MEMBER_LEVEL;
import static com.dnd.runus.jooq.Tables.RUNNING_RECORD;
import static com.dnd.runus.jooq.Tables.SCALE_ACHIEVEMENT;
import static com.dnd.runus.jooq.Tables.SOCIAL_PROFILE;
import static com.dnd.runus.jooq.tables.Member.MEMBER;
import static org.jooq.impl.DSL.select;

/**
 * 회원 탈퇴(hard delete)를 위한 임시 리파지토리
 * FIXME soft delete 완료 후 삭제
 */
@Repository
@RequiredArgsConstructor
public class JooqOauthRepository {
    private final DSLContext dsl;

    public void deleteAllDataAboutMember(long memberId) {
        // 1. CHALLENGE_ACHIEVEMENT_PERCENTAGE 삭제
        dsl.deleteFrom(CHALLENGE_ACHIEVEMENT_PERCENTAGE)
                .where(CHALLENGE_ACHIEVEMENT_PERCENTAGE.CHALLENGE_ACHIEVEMENT_ID.in(select(CHALLENGE_ACHIEVEMENT.ID)
                        .from(CHALLENGE_ACHIEVEMENT)
                        .where(CHALLENGE_ACHIEVEMENT.RUNNING_RECORD_ID.in(dsl.select(RUNNING_RECORD.ID)
                                .from(RUNNING_RECORD)
                                .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))))))
                .execute();

        // 2. CHALLENGE_ACHIEVEMENT, GOAL_ACHIEVEMENT 삭제
        dsl.deleteFrom(CHALLENGE_ACHIEVEMENT)
                .where(CHALLENGE_ACHIEVEMENT.RUNNING_RECORD_ID.in(dsl.select(RUNNING_RECORD.ID)
                        .from(RUNNING_RECORD)
                        .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))))
                .execute();
        dsl.deleteFrom(GOAL_ACHIEVEMENT)
                .where(GOAL_ACHIEVEMENT.RUNNING_RECORD_ID.in(dsl.select(RUNNING_RECORD.ID)
                        .from(RUNNING_RECORD)
                        .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))))
                .execute();

        // 3. RUNNING_RECORD, SCALE_ACHIEVEMENT, MEMBER_LEVEL, SOCIAL_PROFILE 삭제
        dsl.deleteFrom(RUNNING_RECORD)
                .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))
                .execute();
        dsl.deleteFrom(SCALE_ACHIEVEMENT)
                .where(SCALE_ACHIEVEMENT.MEMBER_ID.eq(memberId))
                .execute();
        dsl.deleteFrom(MEMBER_LEVEL).where(MEMBER_LEVEL.MEMBER_ID.eq(memberId)).execute();
        dsl.deleteFrom(SOCIAL_PROFILE)
                .where(SOCIAL_PROFILE.MEMBER_ID.eq(memberId))
                .execute();

        // 4.MEMBER 삭제
        dsl.deleteFrom(MEMBER).where(MEMBER.ID.eq(memberId)).execute();
    }
}
