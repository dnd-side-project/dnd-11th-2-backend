package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.badge.JpaBadgeAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BadgeAchievementRepositoryImpl implements BadgeAchievementRepository {

    private final JpaBadgeAchievementRepository jpaBadgeAchievementRepository;

    @Override
    public void deleteByMemberId(long memberId) {
        jpaBadgeAchievementRepository.deleteByMemberId(memberId);
    }
}
