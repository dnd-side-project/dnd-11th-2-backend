package com.dnd.runus.infrastructure.persistence.jooq.running;

import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.RecordMapper;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.jooq.Tables.RUNNING_RECORD;
import static org.jooq.impl.DSL.cast;
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

    /**
     * 기간 안의 일별 달린 거리을 리턴합니다.
     *
     * @param startDate 시작 날짜 (정각)
     * @param nextDateOfEndDate 종료 날짜의 다음 날 (정각)
     * @return 기간 내 각 날짜별 달린 거리 합계를 포함한 리스트.
     *          각 요소는 날짜와 해당 날짜의 거리 합계를 나타내는 {@link DailyRunningRecordSummary} 객체입니다.
     */
    public List<DailyRunningRecordSummary> findDailyDistancesMeterByDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {

        return dsl.select(
                        cast(RUNNING_RECORD.START_AT, SQLDataType.DATE).as("date"),
                        sum(RUNNING_RECORD.DISTANCE_METER).cast(Integer.class).as("sum_value"))
                .from(RUNNING_RECORD)
                .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))
                .and(RUNNING_RECORD.START_AT.ge(startDate))
                .and(RUNNING_RECORD.START_AT.le(nextDateOfEndDate))
                .groupBy(cast(RUNNING_RECORD.START_AT, SQLDataType.DATE))
                .orderBy(cast(RUNNING_RECORD.START_AT, SQLDataType.DATE))
                .fetch(new DailyRunningSummary());
    }

    /**
     * 기간 안의 일별 달린 시간을 리턴합니다.
     *
     * @param startDate 시작 날짜 (정각)
     * @param nextDateOfEndDate 종료 날짜의 다음 날 (정각)
     * @return 기간 내 각 날짜별 달린 시간 합계를 포함한 리스트.
     *          각 요소는 날짜와 해당 날짜의 달린 시간 합계를 나타내는 {@link DailyRunningRecordSummary} 객체입니다.
     */
    public List<DailyRunningRecordSummary> findDailyDurationsSecMeterByDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {

        return dsl.select(
                        cast(RUNNING_RECORD.START_AT, SQLDataType.DATE).as("date"),
                        sum(RUNNING_RECORD.DURATION_SECONDS).cast(Integer.class).as("sum_value"))
                .from(RUNNING_RECORD)
                .where(RUNNING_RECORD.MEMBER_ID.eq(memberId))
                .and(RUNNING_RECORD.START_AT.ge(startDate))
                .and(RUNNING_RECORD.START_AT.le(nextDateOfEndDate))
                .groupBy(cast(RUNNING_RECORD.START_AT, SQLDataType.DATE))
                .orderBy(cast(RUNNING_RECORD.START_AT, SQLDataType.DATE))
                .fetch(new DailyRunningSummary());
    }

    private static class DailyRunningSummary implements RecordMapper<Record, DailyRunningRecordSummary> {
        @Override
        public DailyRunningRecordSummary map(Record record) {
            return new DailyRunningRecordSummary(
                    record.get("date", LocalDate.class), record.get("sum_value", Integer.class));
        }
    }
}
