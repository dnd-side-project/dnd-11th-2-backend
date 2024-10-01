package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import com.dnd.runus.infrastructure.persistence.jooq.badge.JooqBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepository {
    private final JooqBadgeRepository jooqBadgeRepository;

    @Override
    public List<BadgeWithAchieveStatusAndAchievedAt> findAllBadgesWithAchieveStatusByMemberId(long memberId) {
        return jooqBadgeRepository.findAllBadgesWithAchieveStatusByMemberId(memberId);
    }
}
