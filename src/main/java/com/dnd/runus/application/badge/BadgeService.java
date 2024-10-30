package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.*;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadge;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse.BadgeWithAchievedStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
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

        List<BadgeWithAchieveStatusAndAchievedAt> allBadges =
                badgeRepository.findAllBadgesWithAchieveStatusByMemberId(memberId);

        LocalDateTime oneWeekAgo = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime()
                .minusDays(7)
                .toLocalDateTime();

        EnumMap<BadgeType, List<BadgeWithAchievedStatus>> badgeMap = allBadges.stream()
                .collect(Collectors.groupingBy(
                        v -> v.badge().type(),
                        () -> new EnumMap<>(BadgeType.class),
                        Collectors.mapping(BadgeWithAchievedStatus::from, Collectors.toList())));

        List<BadgeWithAchievedStatus> recencyBadges = allBadges.stream()
                .filter(badge -> isRecent(badge, oneWeekAgo))
                .map(BadgeWithAchievedStatus::from)
                .toList();

        return new AllBadgesListResponse(
                recencyBadges,
                badgeMap.getOrDefault(BadgeType.PERSONAL_RECORD, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.DISTANCE_METER, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.STREAK, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.DURATION_SECONDS, Collections.emptyList()),
                badgeMap.getOrDefault(BadgeType.LEVEL, Collections.emptyList()));
    }

    private boolean isRecent(BadgeWithAchieveStatusAndAchievedAt badge, LocalDateTime criterionDate) {
        return badge.isAchieved() && criterionDate.isBefore(badge.achievedAt());
    }
}
