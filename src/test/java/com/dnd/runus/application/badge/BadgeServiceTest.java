package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadge;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse.BadgesWithType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("뱃지 타입 별 획득한 뱃지 리스트 응답한다. 타입에 대해 획득한 뱃지가 없는 경우, 빈리스트를 반환한다.")
    void getListOfAllBadges() {
        // given
        Badge badge1 = new Badge(1L, "badge1", "description", "imageUrl1", BadgeType.STREAK, 100);
        Badge badge2 = new Badge(2L, "badge2", "description", "imageUrl2", BadgeType.DISTANCE_METER, 1000);

        given(badgeAchievementRepository.findByMemberIdOrderByBadgeTypeAndAchievedAt(1L))
                .willReturn(List.of(
                        new BadgeAchievement.OnlyBadge(1L, badge1, OffsetDateTime.now(), OffsetDateTime.now()),
                        new BadgeAchievement.OnlyBadge(2L, badge2, OffsetDateTime.now(), OffsetDateTime.now())));

        // when
        List<BadgesWithType> badgesList = badgeService.getListOfAllBadges(1L).badgesList();

        // then
        List<String> typeNames =
                Arrays.stream(BadgeType.values()).map(BadgeType::getName).toList();
        List<String> categories =
                badgesList.stream().map(BadgesWithType::category).toList();
        // BadgeType에 해당되는 category가 모두 존재하는지 확인
        assertTrue(categories.containsAll(typeNames));

        badgesList.forEach(badgesWithType -> {
            if (BadgeType.STREAK.getName().equals(badgesWithType.category())
                    || BadgeType.DISTANCE_METER.getName().equals(badgesWithType.category())) {
                assertEquals(1, badgesWithType.badges().size());
            } else {
                assertEquals(0, badgesWithType.badges().size());
            }
        });
    }
}
