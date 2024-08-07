package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.JpaRunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RunningRecordRepositoryImpl implements RunningRecordRepository {

    private final JpaRunningRecordRepository jpaRunningRecordRepository;

    @Override
    public void deleteByMemberId(long memberId) {
        jpaRunningRecordRepository.deleteByMemberId(memberId);
    }
}
