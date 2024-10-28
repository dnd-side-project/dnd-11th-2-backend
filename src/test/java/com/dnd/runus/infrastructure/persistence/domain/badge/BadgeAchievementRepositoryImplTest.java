package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeAchievementEntity;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
class BadgeAchievementRepositoryImplTest {

    @Autowired
    private BadgeAchievementRepository badgeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void beforeEach() {
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @DisplayName("member에 해당하는 badgeAchievement을 삭제한다.")
    @Test
    public void deleteByMember() {
        // given
        Badge badge = new Badge(1L, "testBadge", "testBadge", "tesUrl", BadgeType.DISTANCE_METER, 2);
        BadgeAchievement badgeAchievement = badgeAchievementRepository.save(new BadgeAchievement(badge, savedMember));

        // when
        badgeAchievementRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(badgeAchievementRepository
                .findById(badgeAchievement.badgeAchievementId())
                .isPresent());
    }

    @Nested
    @DisplayName("BadgeAchievement 저장 테스트")
    class BadgeAchievementSaveTest {
        @Autowired
        private EntityManager entityManager;

        private Badge badge1;
        private Badge badge2;

        private BadgeAchievement badgeAchievement1;
        private BadgeAchievement badgeAchievement2;

        @BeforeEach
        void beforeEach() {
            BadgeEntity badgeEntity1 =
                    BadgeEntity.from(new Badge(0L, "testBadge1", "testBadge1", "tesUrl1", BadgeType.DISTANCE_METER, 0));
            BadgeEntity badgeEntity2 =
                    BadgeEntity.from(new Badge(0L, "testBadge2", "testBadge2", "tesUrl2", BadgeType.DISTANCE_METER, 2));

            entityManager.persist(badgeEntity1);
            entityManager.persist(badgeEntity2);

            badge1 = badgeEntity1.toDomain();
            badge2 = badgeEntity2.toDomain();

            badgeAchievement1 = new BadgeAchievement(badge1, savedMember);
            badgeAchievement2 = new BadgeAchievement(badge2, savedMember);

            badgeAchievementRepository.save(badgeAchievement1);
            badgeAchievementRepository.save(badgeAchievement2);
        }

        @AfterEach
        void afterEach() {
            entityManager.createQuery("delete from badge_achievement").executeUpdate();
            entityManager.createQuery("delete from badge").executeUpdate();
        }

        @Test
        @DisplayName("saveAllIgnoreDuplicated: 중복된 데이터가 없을 때 모든 데이터를 저장한다.")
        void saveAllIgnoreDuplicated() {
            // given
            List<BadgeAchievement> badgeAchievements = List.of(badgeAchievement1, badgeAchievement2);

            // when
            badgeAchievementRepository.saveAllIgnoreDuplicated(badgeAchievements);

            // then
            List<BadgeAchievement> achievements = entityManager
                    .createQuery("select ba from badge_achievement ba", BadgeAchievementEntity.class)
                    .getResultList()
                    .stream()
                    .map(BadgeAchievementEntity::toDomain)
                    .toList();

            assertFalse(achievements.isEmpty());

            assertEquals(2, achievements.size());
            assertTrue(achievements.stream().anyMatch(ba -> ba.badge().badgeId() == badge1.badgeId()));
            assertTrue(achievements.stream().anyMatch(ba -> ba.badge().badgeId() == badge2.badgeId()));
        }

        @Test
        @DisplayName("saveAllIgnoreDuplicated: 중복된 데이터가 있을 때 중복된 데이터는 무시하고 저장한다.")
        void saveAllIgnoreDuplicated_case_duplicated() {
            // given
            List<BadgeAchievement> badgeAchievements = List.of(badgeAchievement1, badgeAchievement2);

            // when
            // 저장 여러번 시도
            badgeAchievementRepository.saveAllIgnoreDuplicated(badgeAchievements);
            badgeAchievementRepository.saveAllIgnoreDuplicated(badgeAchievements);

            // then
            List<BadgeAchievement> achievements = entityManager
                    .createQuery("select ba from badge_achievement ba", BadgeAchievementEntity.class)
                    .getResultList()
                    .stream()
                    .map(BadgeAchievementEntity::toDomain)
                    .toList();

            assertFalse(achievements.isEmpty());

            assertEquals(2, achievements.size());
            assertTrue(achievements.stream().anyMatch(ba -> ba.badge().badgeId() == badge1.badgeId()));
            assertTrue(achievements.stream().anyMatch(ba -> ba.badge().badgeId() == badge2.badgeId()));
        }
    }
}
