package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import com.dnd.runus.infrastructure.persistence.jpa.running.JpaRunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RunningRecordRepositoryImpl implements RunningRecordRepository {

    private final JpaRunningRecordRepository jpaRunningRecordRepository;

    @Override
    public void deleteByMember(Member member) {
        jpaRunningRecordRepository.deleteByMember(MemberEntity.from(member));
    }
}
