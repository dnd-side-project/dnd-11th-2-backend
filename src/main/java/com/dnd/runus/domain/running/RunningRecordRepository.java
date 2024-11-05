package com.dnd.runus.domain.running;

import com.dnd.runus.domain.member.Member;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository {
    Optional<RunningRecord> findById(long runningRecordId);

    RunningRecord save(RunningRecord runningRecord);

    void deleteByMemberId(long memberId);

    List<RunningRecord> findByMemberIdAndStartAtBetween(
            long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    boolean hasByMemberIdAndStartAtBetween(long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    int findTotalDistanceMeterByMemberIdWithRangeDate(long memberId, OffsetDateTime startDate, OffsetDateTime endDate);

    Duration findTotalDurationByMemberId(long memberId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<RunningRecord> findByMember(Member member);

    List<DailyRunningRecordSummary> findDailyDistancesMeterByDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    List<DailyRunningRecordSummary> findDailyDurationsSecByDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    int findAvgDistanceMeterByMemberIdAndDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    int findAvgDurationSecByMemberIdAndDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);
}
