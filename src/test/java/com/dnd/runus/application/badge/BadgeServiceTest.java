package com.dnd.runus.application.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {
    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeAchievementRepository badgeAchievementRepository;

    @Test
    @DisplayName("자신이 획득한 뱃지 전체 조회에 성공")
    void getAchievedBadges() {
        // given
        Badge badge1 = new Badge(1L, "badge1", "description", "imageUrl1", BadgeType.RUNNING_COUNT, 100);
        Badge badge2 = new Badge(2L, "badge2", "description", "imageUrl2", BadgeType.DISTANCE_METER, 1000);

        given(badgeAchievementRepository.findByMemberIdWithBadge(1L))
                .willReturn(List.of(
                        new BadgeAchievement.OnlyBadge(1, badge1, OffsetDateTime.now(), OffsetDateTime.now()),
                        new BadgeAchievement.OnlyBadge(2, badge2, OffsetDateTime.now(), OffsetDateTime.now())));

        // when
        AchievedBadgesResponse achievedBadgesResponse = badgeService.getAchievedBadges(1L);

        // then
        List<AchievedBadgesResponse.AchievedBadge> achievedBadges = achievedBadgesResponse.badges();
        AchievedBadgesResponse.AchievedBadge achievedBadge1 = achievedBadges.get(0);
        AchievedBadgesResponse.AchievedBadge achievedBadge2 = achievedBadges.get(1);
        assertEquals("badge1", achievedBadge1.name());
        assertEquals("imageUrl1", achievedBadge1.imageUrl());
        assertEquals("badge2", achievedBadge2.name());
        assertEquals("imageUrl2", achievedBadge2.imageUrl());
    }

    @Test
    @DisplayName("자신이 획득한 뱃지가 없다면, 뱃지가 없는 응답을 반환한다.")
    void getAchievedBadges_Empty() {
        // given
        given(badgeAchievementRepository.findByMemberIdWithBadge(1L)).willReturn(List.of());

        // when
        AchievedBadgesResponse achievedBadgesResponse = badgeService.getAchievedBadges(1L);

        // then
        List<AchievedBadgesResponse.AchievedBadge> achievedBadges = achievedBadgesResponse.badges();
        assertEquals(0, achievedBadges.size());
    }
}
