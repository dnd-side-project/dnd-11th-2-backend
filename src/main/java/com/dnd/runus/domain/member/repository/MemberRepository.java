package com.dnd.runus.domain.member.repository;

import com.dnd.runus.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
