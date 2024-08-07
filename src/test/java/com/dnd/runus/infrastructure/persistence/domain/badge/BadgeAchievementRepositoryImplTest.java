package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.badge.JpaBadgeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeAchievementEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RepositoryTest
class BadgeAchievementRepositoryImplTest {

    @Autowired
    private BadgeAchievementRepository badgeAchievementRepository;

    @Autowired
    private JpaBadgeAchievementRepository jpaBadgeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static Member savedMember;

    @BeforeEach
    void beforeEach() {
        // BadgeAchievement는 Member의 자식임으로 테스트 시 임의이 Member 추가
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @DisplayName("member에 해당하는 badgeAchievement을 삭제한다.")
    @Test
    public void deleteByMember() {
        // given
        Badge badge = new Badge(1L, "testBadge", "testBadge", "tesUrl", BadgeType.DISTANCE_METER, 2);
        BadgeAchievement badgeAchievement = new BadgeAchievement(badge, savedMember);
        BadgeAchievementEntity savedBadgeAchievement =
                jpaBadgeAchievementRepository.save(BadgeAchievementEntity.from(badgeAchievement));

        // when
        badgeAchievementRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(jpaBadgeAchievementRepository
                .findById(savedBadgeAchievement.getId())
                .isPresent());
    }
}
