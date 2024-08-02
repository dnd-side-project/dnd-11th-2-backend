package com.dnd.runus.infrastructure.persistence.jpa.member;

import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<MemberEntity, Long> {}
