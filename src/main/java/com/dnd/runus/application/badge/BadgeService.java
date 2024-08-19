package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeAchievementRepository badgeAchievementRepository;

    public AchievedBadgesResponse getAchievedBadges(long memberId) {
        return new AchievedBadgesResponse(badgeAchievementRepository.findByMemberIdWithBadge(memberId).stream()
                .map(badgeAchievement -> new AchievedBadgesResponse.AchievedBadge(
                        badgeAchievement.badge().badgeId(),
                        badgeAchievement.badge().name(),
                        badgeAchievement.badge().imageUrl(),
                        badgeAchievement.createdAt().toLocalDateTime()))
                .toList());
    }
}
