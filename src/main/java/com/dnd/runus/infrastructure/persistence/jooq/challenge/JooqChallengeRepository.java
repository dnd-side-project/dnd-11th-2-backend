package com.dnd.runus.infrastructure.persistence.jooq.challenge;

import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;

import static com.dnd.runus.jooq.Tables.CHALLENGE;
import static com.dnd.runus.jooq.Tables.CHALLENGE_GOAL_CONDITION;
import static org.jooq.impl.DSL.jsonArrayAgg;
import static org.jooq.impl.DSL.jsonObject;
import static org.jooq.impl.DSL.key;

@Repository
@RequiredArgsConstructor
public class JooqChallengeRepository {

    private final DSLContext dsl;

    public List<ChallengeData.Challenge> findAllIsNotDefeatYesterday() {
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

    public ChallengeData findChallengeWithConditionsBy(long challengeId) {
        return dsl.select(
                        CHALLENGE.ID,
                        CHALLENGE.NAME,
                        CHALLENGE.IMAGE_URL,
                        CHALLENGE.CHALLENGE_TYPE,
                        jsonArrayAgg(jsonObject(
                                        key("goalType").value(CHALLENGE_GOAL_CONDITION.GOAL_TYPE),
                                        key("goalValue").value(CHALLENGE_GOAL_CONDITION.GOAL_VALUE),
                                        key("comparisonValue").value(CHALLENGE_GOAL_CONDITION.GOAL_VALUE),
                                        key("comparisonType").value(CHALLENGE_GOAL_CONDITION.COMPARISON_TYPE)))
                                .as("conditions"))
                .from(CHALLENGE)
                .leftOuterJoin(CHALLENGE_GOAL_CONDITION)
                .on(CHALLENGE.ID.eq(CHALLENGE_GOAL_CONDITION.CHALLENGE_ID))
                .where(CHALLENGE.ID.eq(challengeId))
                .groupBy(CHALLENGE.ID)
                .fetch(new ChallengeDataMapper())
                .get(0);
    }

    private static class ChallengeMapper implements RecordMapper<Record, ChallengeData.Challenge> {

        @Override
        public ChallengeData.Challenge map(Record record) {
            return new ChallengeData.Challenge(
                    record.get(CHALLENGE.ID, long.class),
                    record.get(CHALLENGE.NAME, String.class),
                    record.get(CHALLENGE.EXPECTED_TIME, int.class),
                    record.get(CHALLENGE.IMAGE_URL, String.class),
                    record.get(CHALLENGE.CHALLENGE_TYPE, ChallengeType.class));
        }
    }

    private static class ChallengeDataMapper implements RecordMapper<Record, ChallengeData> {
        @Override
        public ChallengeData map(Record record) {
            return new ChallengeData(
                    new ChallengeData.Challenge(
                            record.get(CHALLENGE.ID, long.class),
                            record.get(CHALLENGE.NAME, String.class),
                            record.get(CHALLENGE.IMAGE_URL, String.class),
                            record.get(CHALLENGE.CHALLENGE_TYPE, ChallengeType.class)),
                    parseConditions(record.get("conditions", String.class)));
        }

        private List<ChallengeCondition> parseConditions(String conditionsArrays) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ChallengeCondition>>() {}.getType();

            return gson.fromJson(conditionsArrays, listType);
        }
    }
}
