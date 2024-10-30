package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadge;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse.BadgeWithAchievedStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {
    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeAchievementRepository badgeAchievementRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Test
    @DisplayName("자신이 획득한 뱃지 전체 조회에 성공")
    void getAchievedBadges() {
        // given
        Badge badge1 = new Badge(1L, "badge1", "description", "imageUrl1", BadgeType.STREAK, 100);
        Badge badge2 = new Badge(2L, "badge2", "description", "imageUrl2", BadgeType.DISTANCE_METER, 1000);

        given(badgeAchievementRepository.findByMemberIdWithBadgeOrderByAchievedAtLimit(1L, 3))
                .willReturn(List.of(
                        new BadgeAchievement.OnlyBadge(1, badge1, OffsetDateTime.now(), OffsetDateTime.now()),
                        new BadgeAchievement.OnlyBadge(2, badge2, OffsetDateTime.now(), OffsetDateTime.now())));

        // when
        AchievedBadgesResponse achievedBadgesResponse = badgeService.getAchievedBadges(1L);

        // then
        List<AchievedBadge> achievedBadges = achievedBadgesResponse.badges();
        AchievedBadge achievedBadge1 = achievedBadges.get(0);
        AchievedBadge achievedBadge2 = achievedBadges.get(1);
        assertEquals("badge1", achievedBadge1.name());
        assertEquals("imageUrl1", achievedBadge1.imageUrl());
        assertEquals("badge2", achievedBadge2.name());
        assertEquals("imageUrl2", achievedBadge2.imageUrl());
    }

    @Test
    @DisplayName("자신이 획득한 뱃지가 없다면, 뱃지가 없는 응답을 반환한다.")
    void getAchievedBadges_Empty() {
        // given
        given(badgeAchievementRepository.findByMemberIdWithBadgeOrderByAchievedAtLimit(1L, 3))
                .willReturn(List.of());

        // when
        AchievedBadgesResponse achievedBadgesResponse = badgeService.getAchievedBadges(1L);

        // then
        List<AchievedBadge> achievedBadges = achievedBadgesResponse.badges();
        assertEquals(0, achievedBadges.size());
    }

    @DisplayName("모든 배지 리스트를 조회 한다.")
    @Test
    void findAllBadges() {
        // given
        long memberId = 1L;
        LocalDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID).atTime(0, 0, 0);
        given(badgeRepository.findAllBadgesWithAchieveStatusByMemberId(memberId))
                .willReturn(List.of(
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(1L, "streak_today_achieved", "description", "imageUrl", BadgeType.STREAK, 0),
                                true,
                                todayMidnight),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(
                                        1L, "streak_6daysAgo_achieved", "description", "imageUrl", BadgeType.STREAK, 0),
                                true,
                                todayMidnight.minusDays(6)),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(
                                        1L, "streak_8daysAgo_achieved", "description", "imageUrl", BadgeType.STREAK, 0),
                                true,
                                todayMidnight.minusDays(8)),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(1L, "streak_NotAchieved", "description", "imageUrl", BadgeType.STREAK, 0),
                                false,
                                null),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(1L, "distance", "description", "imageUrl", BadgeType.DISTANCE_METER, 0),
                                false,
                                null),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(1L, "personal", "description", "imageUrl", BadgeType.PERSONAL_RECORD, 0),
                                false,
                                null),
                        new BadgeWithAchieveStatusAndAchievedAt(
                                new Badge(1L, "duration", "description", "imageUrl", BadgeType.DURATION_SECONDS, 0),
                                false,
                                null)));

        // when
        AllBadgesListResponse result = badgeService.getListOfAllBadges(memberId);

        // then
        List<BadgeWithAchievedStatus> recencyBadges = result.recencyBadges();
        List<BadgeWithAchievedStatus> streakBadges = result.streakBadges();
        List<BadgeWithAchievedStatus> distanceBadges = result.distanceBadges();
        List<BadgeWithAchievedStatus> personalBadges = result.personalBadges();
        List<BadgeWithAchievedStatus> durationBadges = result.durationBadges();

        assertFalse(recencyBadges.isEmpty());
        assertFalse(streakBadges.isEmpty());
        assertFalse(distanceBadges.isEmpty());
        assertFalse(personalBadges.isEmpty());
        assertFalse(durationBadges.isEmpty());
        assertTrue(result.levelBadges().isEmpty());

        assertThat(recencyBadges.size()).isEqualTo(2);

        assertThat(streakBadges.size()).isEqualTo(4);
        streakBadges.forEach(v -> {
            if (v.name().contains("achieved")) {
                assertTrue(v.isAchieved());
            } else {
                assertFalse(v.isAchieved());
            }
        });

        assertThat(distanceBadges.size()).isEqualTo(1);
        assertThat(personalBadges.size()).isEqualTo(1);
        assertThat(durationBadges.size()).isEqualTo(1);
    }
}
