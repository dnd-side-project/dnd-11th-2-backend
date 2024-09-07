package com.dnd.runus.infrastructure.persistence.jooq.scale;

import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

import static com.dnd.runus.jooq.Tables.SCALE;
import static com.dnd.runus.jooq.Tables.SCALE_ACHIEVEMENT;

@Repository
@RequiredArgsConstructor
public class JooqScaleAchievementRepository {

    private final DSLContext dsl;

    public List<ScaleAchievementLog> findScaleAchievementLogs(long memberId) {
        return dsl
                .select(
                        SCALE.ID,
                        SCALE.NAME,
                        SCALE.SIZE_METER,
                        SCALE.INDEX,
                        SCALE.START_NAME,
                        SCALE.END_NAME,
                        SCALE_ACHIEVEMENT.ACHIEVED_DATE)
                .from(SCALE)
                .leftJoin(SCALE_ACHIEVEMENT)
                .on(SCALE.ID.eq(SCALE_ACHIEVEMENT.SCALE_ID).and(SCALE_ACHIEVEMENT.MEMBER_ID.eq(memberId)))
                .fetch(new ScaleAchievementLogMapper())
                .stream()
                .sorted(Comparator.comparingInt(log -> log.scale().index()))
                .toList();
    }

    private static class ScaleAchievementLogMapper implements RecordMapper<Record, ScaleAchievementLog> {
        @Override
        public ScaleAchievementLog map(Record record) {
            return new ScaleAchievementLog(
                    new Scale(
                            record.get(SCALE.ID),
                            record.get(SCALE.NAME),
                            record.get(SCALE.SIZE_METER),
                            record.get(SCALE.INDEX),
                            record.get(SCALE.START_NAME),
                            record.get(SCALE.END_NAME)),
                    record.get(SCALE_ACHIEVEMENT.ACHIEVED_DATE));
        }
    }
}
