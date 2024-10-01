package com.dnd.runus.domain.badge;

import java.util.List;

public interface BadgeRepository {
    List<BadgeWithAchieveStatusAndAchievedAt> findAllBadgesWithAchieveStatusByMemberId(long memberId);
}
