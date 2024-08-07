package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.JpaRunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunningRecordRepositoryImpl implements RunningRecordRepository {

    private final JpaRunningRecordRepository jpaRunningRecordRepository;

    @Override
    public Optional<RunningRecord> findById(long runningRecordId) {
        return jpaRunningRecordRepository.findById(runningRecordId).map(RunningRecordEntity::toDomain);
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
