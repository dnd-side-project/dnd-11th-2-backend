package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jooq.running.JooqRunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.JpaRunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RunningRecordRepositoryImpl implements RunningRecordRepository {

    private final JpaRunningRecordRepository jpaRunningRecordRepository;
    private final JooqRunningRecordRepository jooqRunningRecordRepository;

    @Override
    public Optional<RunningRecord> findById(long runningRecordId) {
        return jpaRunningRecordRepository.findById(runningRecordId).map(RunningRecordEntity::toDomain);
    }

    @Override
    public List<RunningRecord> findByMemberIdAndStartAtBetween(
            long memberId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return jpaRunningRecordRepository.findByMemberIdAndStartAtBetween(memberId, startTime, endTime).stream()
                .map(RunningRecordEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RunningRecord> findByMember(Member member) {
        return jpaRunningRecordRepository.findByMemberId(member.memberId()).stream()
                .map(RunningRecordEntity::toDomain)
                .toList();
    }

    @Override
    public int findTotalDistanceMeterByMemberId(long memberId) {
        return jooqRunningRecordRepository.findTotalDistanceMeterByMemberId(memberId);
    }

    @Override
    public int findTotalDistanceMeterByMemberIdWithRangeDate(
            long memberId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return jooqRunningRecordRepository.findTotalDistanceMeterByMemberIdWithRangeDate(memberId, startDate, endDate);
    }

    @Override
    public Duration findTotalDurationByMemberId(long memberId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return Duration.ofSeconds(
                jooqRunningRecordRepository.findTotalDurationByMemberId(memberId, startDate, endDate));
    }

    @Override
    public List<DailyRunningRecordSummary> findDailyDistancesMeterWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {
        return jooqRunningRecordRepository.findDailyDistancesMeterWithDateRange(memberId, startDate, nextDateOfEndDate);
    }

    @Override
    public List<DailyRunningRecordSummary> findDailyDurationsSecWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {
        return jooqRunningRecordRepository.findDailyDurationsSecMeterWithDateRange(
                memberId, startDate, nextDateOfEndDate);
    }

    @Override
    public int findAvgDistanceMeterByMemberIdWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {
        return jooqRunningRecordRepository.findAvgDistanceMeterByMemberIdWithDateRange(
                memberId, startDate, nextDateOfEndDate);
    }

    @Override
    public int findAvgDurationSecByMemberIdWithDateRange(
            long memberId, OffsetDateTime startDate, OffsetDateTime nextDateOfEndDate) {
        return jooqRunningRecordRepository.findAvgDurationSecByMemberIdWithDateRange(
                memberId, startDate, nextDateOfEndDate);
    }

    @Override
    public boolean hasByMemberIdAndStartAtBetween(long memberId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return jpaRunningRecordRepository.existsByMemberIdAndStartAtBetween(memberId, startTime, endTime);
    }

    @Override
    public RunningRecord save(RunningRecord runningRecord) {
        return jpaRunningRecordRepository
                .save(RunningRecordEntity.from(runningRecord))
                .toDomain();
    }

    @Override
    public void deleteByMemberId(long memberId) {
        jpaRunningRecordRepository.deleteByMemberId(memberId);
    }
}
