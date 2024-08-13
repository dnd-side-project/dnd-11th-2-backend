package com.dnd.runus.domain.badge;

import java.util.Optional;

public interface BadgeAchievementRepository {

    Optional<BadgeAchievement> findById(long id);

    BadgeAchievement save(BadgeAchievement badgeAchievement);

    void deleteByMemberId(long memberId);
}
