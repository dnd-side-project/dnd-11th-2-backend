package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import com.dnd.runus.domain.badge.BadgeWithAchievedStatusAndRecentlyStatus;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final BadgeRepository badgeRepository;

    public AchievedBadgesResponse getAchievedBadges(long memberId) {
        return new AchievedBadgesResponse(badgeAchievementRepository.findByMemberIdWithBadge(memberId).stream()
                .map(badgeAchievement -> new AchievedBadgesResponse.AchievedBadge(
                        badgeAchievement.badge().badgeId(),
                        badgeAchievement.badge().name(),
                        badgeAchievement.badge().imageUrl(),
                        badgeAchievement.createdAt().toLocalDateTime()))
                .toList());
    }

    @Transactional(readOnly = true)
    public AllBadgesListResponse getListOfAllBadges(long memberId) {

        List<BadgeWithAchieveStatusAndAchievedAt> allBadges =
                badgeRepository.findAllBadgesWithAchieveStatusByMemberId(memberId);

        Map<BadgeType, List<AllBadgesListResponse.BadgeWithAchievedStatus>> badgeMap = new EnumMap<>(BadgeType.class);
        List<AllBadgesListResponse.BadgeWithAchievedStatus> recencyBadges = new ArrayList<>();

        OffsetDateTime oneWeekAgo = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime()
                .minusDays(7);

        allBadges.forEach(badge -> {
            BadgeWithAchievedStatusAndRecentlyStatus badgeMapWithRecentlyStatus =
                    BadgeWithAchievedStatusAndRecentlyStatus.from(badge, oneWeekAgo);
            badgeMap.computeIfAbsent(badgeMapWithRecentlyStatus.badgeType(), k -> new ArrayList<>())
                    .add(badgeMapWithRecentlyStatus.badgeWithAchievedStatus());
            if (badgeMapWithRecentlyStatus.isRecent()) {
                recencyBadges.add(badgeMapWithRecentlyStatus.badgeWithAchievedStatus());
            }
        });
        return new AllBadgesListResponse(
                recencyBadges,
                badgeMap.getOrDefault(BadgeType.PERSONAL_RECORD, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.DISTANCE_METER, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.STREAK, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.DURATION_SECONDS, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.LEVEL, Collections.emptyList()));
    }
}
