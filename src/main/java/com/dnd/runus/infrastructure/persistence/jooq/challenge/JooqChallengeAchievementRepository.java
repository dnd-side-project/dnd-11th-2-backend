package com.dnd.runus.infrastructure.persistence.jooq.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.dnd.runus.jooq.Tables.CHALLENGE;
import static com.dnd.runus.jooq.Tables.CHALLENGE_ACHIEVEMENT;

@Repository
@RequiredArgsConstructor
public class JooqChallengeAchievementRepository {
    private final DSLContext dsl;

    public ChallengeAchievement.Status findStatusByRunningRecordId(long runningRecordId) {
        return dsl.select(
                        CHALLENGE_ACHIEVEMENT.ID,
                        CHALLENGE_ACHIEVEMENT.SUCCESS_STATUS,
                        CHALLENGE.ID,
                        CHALLENGE.NAME,
                        CHALLENGE.EXPECTED_TIME,
                        CHALLENGE.IMAGE_URL,
                        CHALLENGE.CHALLENGE_TYPE)
                .from(CHALLENGE_ACHIEVEMENT)
                .join(CHALLENGE)
                .on(CHALLENGE_ACHIEVEMENT.CHALLENGE_ID.eq(CHALLENGE.ID))
                .where(CHALLENGE_ACHIEVEMENT.RUNNING_RECORD_ID.eq(runningRecordId))
                .fetchOne(record -> new ChallengeAchievement.Status(
                        record.get(CHALLENGE_ACHIEVEMENT.ID, Long.class),
                        new Challenge(
                                record.get(CHALLENGE.ID, Long.class),
                                record.get(CHALLENGE.NAME, String.class),
                                record.get(CHALLENGE.EXPECTED_TIME, Integer.class),
                                record.get(CHALLENGE.IMAGE_URL, String.class),
                                ChallengeType.valueOf(record.get(CHALLENGE.CHALLENGE_TYPE, String.class))),
                        record.get(CHALLENGE_ACHIEVEMENT.SUCCESS_STATUS)));
    }
}
