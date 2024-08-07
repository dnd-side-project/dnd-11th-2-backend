package com.dnd.runus.infrastructure.persistence.jpa.member;

import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberLevelRepository extends JpaRepository<MemberLevelEntity, Long> {

    void deleteByMemberId(long memberId);
}
