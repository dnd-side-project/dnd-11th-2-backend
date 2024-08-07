package com.dnd.runus.infrastructure.persistence.jpa.running;

import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaRunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {
    @Transactional
    void deleteByMember(MemberEntity member);
}
