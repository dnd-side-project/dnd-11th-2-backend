package com.dnd.runus.infrastructure.persistence.jooq.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.ComparisonType;
import com.dnd.runus.domain.challenge.GoalMetricType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dnd.runus.jooq.Tables.CHALLENGE;
import static com.dnd.runus.jooq.Tables.CHALLENGE_GOAL_CONDITION;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class JooqChallengeRepository {

    private final DSLContext dsl;

    public List<Challenge> findAllIsNotDefeatYesterday() {
        return dsl.select(
                        CHALLENGE.ID,
                        CHALLENGE.NAME,
                        CHALLENGE.EXPECTED_TIME,
                        CHALLENGE.IMAGE_URL,
                        CHALLENGE.CHALLENGE_TYPE)
                .from(CHALLENGE)
                .where(CHALLENGE.CHALLENGE_TYPE.ne(ChallengeType.DEFEAT_YESTERDAY.toString()))
                .fetch(new ChallengeMapper());
    }

    public ChallengeWithCondition findChallengeWithConditionsBy(long challengeId) {
        return dsl.select(
                        CHALLENGE.ID,
                        CHALLENGE.NAME,
                        CHALLENGE.IMAGE_URL,
                        CHALLENGE.CHALLENGE_TYPE,
                        multiset(select(
                                                CHALLENGE_GOAL_CONDITION.GOAL_TYPE,
                                                CHALLENGE_GOAL_CONDITION.COMPARISON_TYPE,
                                                CHALLENGE_GOAL_CONDITION.GOAL_VALUE)
                                        .from(CHALLENGE_GOAL_CONDITION)
                                        .where(CHALLENGE_GOAL_CONDITION.CHALLENGE_ID.eq(CHALLENGE.ID)))
                                .convertFrom(r -> r.map(record -> new ChallengeCondition(
                                        record.get(CHALLENGE_GOAL_CONDITION.GOAL_TYPE, GoalMetricType.class),
                                        record.get(CHALLENGE_GOAL_CONDITION.COMPARISON_TYPE, ComparisonType.class),
                                        record.get(CHALLENGE_GOAL_CONDITION.GOAL_VALUE, Integer.class))))
                                .as("conditions"))
                .from(CHALLENGE)
                .where(CHALLENGE.ID.eq(challengeId))
                .fetchOne(new ChallengeWithConditionMapper());
    }

    private static class ChallengeMapper implements RecordMapper<Record, Challenge> {

        @Override
        public Challenge map(Record record) {
            return new Challenge(
                    record.get(CHALLENGE.ID, long.class),
                    record.get(CHALLENGE.NAME, String.class),
                    record.get(CHALLENGE.EXPECTED_TIME, int.class),
                    record.get(CHALLENGE.IMAGE_URL, String.class),
                    record.get(CHALLENGE.CHALLENGE_TYPE, ChallengeType.class));
        }
    }

    private static class ChallengeWithConditionMapper implements RecordMapper<Record, ChallengeWithCondition> {
        @Override
        public ChallengeWithCondition map(Record record) {
            if (record.get("conditions") == null) {
                throw new NullPointerException("conditions은 null이 될 수 없습니다.");
            }

            // "conditions"은 List<ChallengeCondition> 타입으로 반환합니다.
            @SuppressWarnings("unchecked") // 타입 안전성 경고 무시
            List<ChallengeCondition> challengeConditions = record.get("conditions", List.class);

            return new ChallengeWithCondition(
                    new Challenge(
                            record.get(CHALLENGE.ID, long.class),
                            record.get(CHALLENGE.NAME, String.class),
                            record.get(CHALLENGE.IMAGE_URL, String.class),
                            record.get(CHALLENGE.CHALLENGE_TYPE, ChallengeType.class)),
                    challengeConditions);
        }
    }
}
