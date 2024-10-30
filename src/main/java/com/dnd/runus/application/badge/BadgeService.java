package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.*;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadge;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final BadgeRepository badgeRepository;

    public void achieveBadge(Member member, BadgeType badgeType, int value) {
        List<Badge> badges = badgeRepository.findByTypeAndRequiredValueLessThanEqual(badgeType, value);
        if (badges.isEmpty()) {
            return;
        }

        List<BadgeAchievement> badgeAchievements = badges.stream()
                .map(badge -> new BadgeAchievement(badge, member))
                .toList();

        badgeAchievementRepository.saveAllIgnoreDuplicated(badgeAchievements);
    }

    public AchievedBadgesResponse getAchievedBadges(long memberId) {
        return new AchievedBadgesResponse(
                badgeAchievementRepository.findByMemberIdWithBadgeOrderByAchievedAtLimit(memberId, 3).stream()
                        .map(badgeAchievement -> new AchievedBadge(
                                badgeAchievement.badge().badgeId(),
                                badgeAchievement.badge().name(),
                                badgeAchievement.badge().imageUrl(),
                                badgeAchievement.createdAt().toLocalDateTime()))
                        .toList());
    }

    @Transactional(readOnly = true)
    public AllBadgesListResponse getListOfAllBadges(long memberId) {

        List<BadgeAchievement.OnlyBadge> allBadges =
                badgeAchievementRepository.findByMemberIdOrderByBadgeTypeAndAchievedAt(memberId);

        // badgeType별 획득한 배지 리스트
        EnumMap<BadgeType, List<AchievedBadge>> badgesWithType = allBadges.stream()
                .collect(Collectors.groupingBy(
                        v -> v.badge().type(),
                        () -> new EnumMap<>(BadgeType.class),
                        Collectors.mapping(
                                v -> new AchievedBadge(
                                        v.badge().badgeId(),
                                        v.badge().name(),
                                        v.badge().imageUrl(),
                                        v.createdAt().toLocalDateTime()),
                                Collectors.toList())));

        // 최신 일주일안에 딴 뱃지를 recencyBadges에 추가
        LocalDateTime oneWeekAgo = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime()
                .minusDays(7)
                .toLocalDateTime();
        List<AchievedBadge> recencyBadges = allBadges.stream()
                .filter(v -> oneWeekAgo.isBefore(v.createdAt().toLocalDateTime()))
                .map(v -> new AchievedBadge(
                        v.badge().badgeId(),
                        v.badge().name(),
                        v.badge().imageUrl(),
                        v.createdAt().toLocalDateTime()))
                .toList();

        return AllBadgesListResponse.from(recencyBadges, badgesWithType);
    }
}
