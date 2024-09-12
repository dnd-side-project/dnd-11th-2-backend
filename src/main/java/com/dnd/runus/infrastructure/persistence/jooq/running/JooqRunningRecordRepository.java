package com.dnd.runus.infrastructure.persistence.jooq.running;

import com.dnd.runus.domain.running.RunningRecordWeeklySummary;
import lombok.RequiredArgsConstructor;
import org.jooq.CommonTableExpression;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.RecordMapper;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.jooq.Tables.RUNNING_RECORD;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static org.jooq.impl.DSL.cast;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;

@Repository
@RequiredArgsConstructor
public class JooqRunningRecordRepository {
    private final DSLContext dsl;

    public int findTotalDistanceMeterByMemberId(long memberId, OffsetDateTime startDate, OffsetDateTime endDate) {
        Record1<Integer> result = dsl.select(sum(RUNNING_RECORD.DISTANCE_METER).cast(Integer.class))
                .from(RUNNING_RECORD)
                .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))
                .and(RUNNING_RECORD.START_AT.ge(startDate))
                .and(RUNNING_RECORD.START_AT.lessThan(endDate))
                .fetchOne();
        if (result != null && result.value1() != null) {
            return result.value1();
        }
        return 0;
    }

    public List<RunningRecordWeeklySummary> findWeeklyDistanceSummaryMeter(long memberId, OffsetDateTime today) {

        int day = today.get(DAY_OF_WEEK) - 1;
        LocalDate startDate = today.minusDays(day).toLocalDate();

        CommonTableExpression<Record1<Date>> dateRange = name("date_range")
                .fields("start_date")
                .as(select(field(
                        "generate_series(?, ?, interval '1 day')",
                        SQLDataType.DATE,
                        startDate,
                        startDate.plusDays(6))));

        return dsl.with(dateRange)
                .select(
                        dateRange.field("start_date", SQLDataType.DATE),
                        sum(RUNNING_RECORD.DISTANCE_METER).cast(Integer.class).as("sum_distance"))
                .from(dateRange)
                .leftJoin(RUNNING_RECORD)
                .on(cast(RUNNING_RECORD.START_AT, SQLDataType.DATE).eq(dateRange.field("start_date", SQLDataType.DATE)))
                .and(RUNNING_RECORD.MEMBER_ID.eq(memberId))
                .groupBy(dateRange.field("start_date"))
                .orderBy(dateRange.field("start_date"))
                .fetch(new RunningWeeklyDistanceSummary());
    }

    private static class RunningWeeklyDistanceSummary implements RecordMapper<Record, RunningRecordWeeklySummary> {
        @Override
        public RunningRecordWeeklySummary map(Record record) {
            return new RunningRecordWeeklySummary(
                    record.get("start_date", LocalDate.class), record.get("sum_distance", Integer.class));
        }
    }
}
