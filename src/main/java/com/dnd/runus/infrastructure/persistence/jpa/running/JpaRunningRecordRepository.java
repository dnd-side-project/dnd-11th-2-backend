package com.dnd.runus.infrastructure.persistence.jpa.running;

import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface JpaRunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {

    void deleteByMemberId(long memberId);

    @EntityGraph(attributePaths = {"member"})
    List<RunningRecordEntity> findByMemberIdAndStartAtBetween(
            long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    boolean existsByMemberIdAndStartAtBetween(long memberId, OffsetDateTime startTime, OffsetDateTime endTime);

    @EntityGraph(attributePaths = {"member"})
    List<RunningRecordEntity> findByMemberId(long memberId);
}
