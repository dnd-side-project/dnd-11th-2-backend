package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.infrastructure.persistence.jpa.badge.JpaBadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BadgeAchievementRepositoryImpl implements BadgeAchievementRepository {
    private final JpaBadgeAchievementRepository jpaBadgeAchievementRepository;

    @Override
    public void deleteByMember(Member member) {
        jpaBadgeAchievementRepository.deleteByMember(MemberEntity.from(member));
    }
}
