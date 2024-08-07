package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaMemberLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberLevelRepositoryImpl implements MemberLevelRepository {

    private final JpaMemberLevelRepository jpaMemberLevelRepository;

    @Override
    public void deleteByMemberId(long memberId) {
        jpaMemberLevelRepository.deleteByMemberId(memberId);
    }
}
