package com.dnd.runus.domain.running;

import com.dnd.runus.domain.member.Member;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository {
    Optional<RunningRecord> findById(long runningRecordId);

    List<RunningRecord> findByMemberIdAndStartAtBetween(
            long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    List<RunningRecord> findByMember(Member member);

    int findTotalDistanceMeterByMemberId(long memberId);

    int findTotalDistanceMeterByMemberIdWithRangeDate(long memberId, OffsetDateTime startDate, OffsetDateTime endDate);

    Duration findTotalDurationByMemberId(long memberId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<DailyRunningRecordSummary> findDailyDistancesMeterWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    List<DailyRunningRecordSummary> findDailyDurationsSecWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    int findAvgDistanceMeterByMemberIdWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    int findAvgDurationSecByMemberIdWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate);

    boolean hasByMemberIdAndStartAtBetween(long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    RunningRecord save(RunningRecord runningRecord);

    void deleteByMemberId(long memberId);
}
