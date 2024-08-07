package com.dnd.runus.infrastructure.persistence.jpa.running;

import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {

    void deleteByMemberId(long memberId);
}
