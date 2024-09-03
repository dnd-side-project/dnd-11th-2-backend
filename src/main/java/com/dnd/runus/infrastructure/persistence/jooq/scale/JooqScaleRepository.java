package com.dnd.runus.infrastructure.persistence.jooq.scale;

import com.dnd.runus.domain.scale.ScaleSummary;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import static com.dnd.runus.jooq.tables.Scale.SCALE;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

@Repository
@RequiredArgsConstructor
public class JooqScaleRepository {

    private final DSLContext dsl;

    public ScaleSummary getSummary() {
        return dsl.select(
                        count().as("count"), coalesce(sum(SCALE.SIZE_METER), 0).as("total_meter"))
                .from(SCALE)
                .fetchOne(new ScaleSummaryMapper());
    }

    private static class ScaleSummaryMapper implements RecordMapper<Record, ScaleSummary> {
        @Override
        public ScaleSummary map(Record record) {
            return new ScaleSummary(record.get("count", int.class), record.get("total_meter", int.class));
        }
    }
}
