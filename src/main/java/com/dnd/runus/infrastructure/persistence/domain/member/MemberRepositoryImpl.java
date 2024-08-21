package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.infrastructure.persistence.jooq.member.JooqMemberRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaMemberRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final JpaMemberRepository jpaMemberRepository;
    private final JooqMemberRepository jooqMemberRepository;

    private final EntityManager em;

    @Override
    public Optional<Member> findById(long id) {
        return jpaMemberRepository.findById(id).map(MemberEntity::toDomain);
    }

    @Override
    public Member save(Member member) {
        return jpaMemberRepository.save(MemberEntity.from(member)).toDomain();
    }

    @Override
    public void deleteById(long memberId) {
        jpaMemberRepository.deleteById(memberId);
    }

    @Override
    public void updateNicknameById(long memberId, String newNickname) {
        em.flush();
        em.clear();
        jooqMemberRepository.updateMemberNickname(memberId, newNickname);
    }
}
