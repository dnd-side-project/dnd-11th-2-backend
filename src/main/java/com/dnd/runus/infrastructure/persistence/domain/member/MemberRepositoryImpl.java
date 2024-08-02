package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaMemberRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public Optional<Member> findById(long id) {
        return jpaMemberRepository.findById(id).map(MemberEntity::toDomain);
    }

    @Override
    public Member save(Member member) {
        return jpaMemberRepository.save(MemberEntity.from(member)).toDomain();
    }
}
