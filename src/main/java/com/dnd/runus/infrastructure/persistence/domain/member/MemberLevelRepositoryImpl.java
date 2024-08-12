package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.infrastructure.persistence.jooq.member.JooqMemberLevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaMemberLevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberLevelRepositoryImpl implements MemberLevelRepository {

    private final JpaMemberLevelRepository jpaMemberLevelRepository;
    private final JooqMemberLevelRepository jooqMemberLevelRepository;

    @Override
    public MemberLevel save(MemberLevel memberLevel) {
        return jpaMemberLevelRepository
                .save(MemberLevelEntity.from(memberLevel))
                .toDomain();
    }

    @Override
    public Optional<MemberLevel> findByMemberId(long memberLevelId) {
        return jpaMemberLevelRepository.findByMemberId(memberLevelId).map(MemberLevelEntity::toDomain);
    }

    @Override
    public void deleteByMemberId(long memberId) {
        jpaMemberLevelRepository.deleteByMemberId(memberId);
    }

    @Override
    public MemberLevel.Summary updateMemberLevel(long memberId, int exp) {
        return jooqMemberLevelRepository.updateMemberLevel(memberId, exp);
    }
}
