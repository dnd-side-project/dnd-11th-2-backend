package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jooq.badge.JooqBadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.badge.JpaBadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BadgeAchievementRepositoryImpl implements BadgeAchievementRepository {

    private final JpaBadgeAchievementRepository jpaBadgeAchievementRepository;
    private final JooqBadgeAchievementRepository jooqBadgeAchievementRepository;

    @Override
    public Optional<BadgeAchievement> findById(long id) {
        return jpaBadgeAchievementRepository.findById(id).map(BadgeAchievementEntity::toDomain);
    }

    @Override
    public List<BadgeAchievement.OnlyBadge> findByMemberIdWithBadgeOrderByAchievedAtLimit(long memberId, int limit) {
        return jooqBadgeAchievementRepository.findByMemberIdWithBadgeOrderByAchievedAtLimit(memberId, limit);
    }

    @Override
    public BadgeAchievement save(BadgeAchievement badgeAchievement) {
        return jpaBadgeAchievementRepository
                .save(BadgeAchievementEntity.from(badgeAchievement))
                .toDomain(badgeAchievement.badge());
    }

    @Override
    public void saveAllIgnoreDuplicated(List<BadgeAchievement> badgeAchievements) {
        jooqBadgeAchievementRepository.saveAllIgnoreDuplicated(badgeAchievements);
    }

    @Override
    public void deleteByMemberId(long memberId) {
        jpaBadgeAchievementRepository.deleteByMemberId(memberId);
    }
}
