package com.dnd.runus.domain.badge;

import java.util.List;
import java.util.Optional;

public interface BadgeAchievementRepository {

    Optional<BadgeAchievement> findById(long id);

    List<BadgeAchievement.OnlyBadge> findByMemberIdWithBadgeOrderByAchievedAtLimit(long memberId, int limit);

    BadgeAchievement save(BadgeAchievement badgeAchievement);

    void saveAllIgnoreDuplicated(List<BadgeAchievement> badgeAchievements);

    void deleteByMemberId(long memberId);
}
