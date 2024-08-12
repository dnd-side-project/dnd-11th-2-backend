package com.dnd.runus.infrastructure.persistence.jpa.member;

import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberLevelRepository extends JpaRepository<MemberLevelEntity, Long> {

    Optional<MemberLevelEntity> findByMemberId(long memberId);

    void deleteByMemberId(long memberId);
}
