package com.dnd.runus.infrastructure.persistence.jpa.member;

import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaMemberLevelRepository extends JpaRepository<MemberLevelEntity, Long> {

    @Transactional
    void deleteByMemberId(long memberId);
}
