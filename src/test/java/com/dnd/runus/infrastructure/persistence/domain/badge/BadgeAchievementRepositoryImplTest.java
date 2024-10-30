package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievement.OnlyBadge;
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

import java.time.OffsetDateTime;
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
    @DisplayName("BadeAchievement 조회 테스트")
    class BadgeAchievementFindTest {
        @Autowired
        private EntityManager entityManager;

        private Badge badge1;
        private Badge badge2;
        private Badge badge3;
        private Badge badge4;

        @BeforeEach
        void beforeEach() {
            BadgeEntity badgeEntity1 =
                    BadgeEntity.from(new Badge(0L, "testBadge1", "testBadge1", "tesUrl1", BadgeType.DISTANCE_METER, 0));
            BadgeEntity badgeEntity2 =
                    BadgeEntity.from(new Badge(0L, "testBadge2", "testBadge2", "tesUrl2", BadgeType.DISTANCE_METER, 2));

            BadgeEntity badgeEntity3 =
                    BadgeEntity.from(new Badge(0L, "testBadge3", "testBadge3", "tesUrl3", BadgeType.STREAK, 3));

            BadgeEntity badgeEntity4 =
                    BadgeEntity.from(new Badge(0L, "testBadge4", "testBadge4", "tesUrl4", BadgeType.STREAK, 4));

            entityManager.persist(badgeEntity1);
            entityManager.persist(badgeEntity2);
            entityManager.persist(badgeEntity3);
            entityManager.persist(badgeEntity4);

            badge1 = badgeEntity1.toDomain();
            badge2 = badgeEntity2.toDomain();
            badge3 = badgeEntity3.toDomain();
            badge4 = badgeEntity4.toDomain();
        }

        @AfterEach
        void afterEach() {
            entityManager.createQuery("delete from badge_achievement").executeUpdate();
            entityManager.createQuery("delete from badge").executeUpdate();
        }

        @Test
        @DisplayName("획득한 뱃지를 조회한다: 획득한 최신 순, 최대 3개의 데이터를 리턴한다.")
        void findByMemberIdWithBadgeOrderByAchievedAtLimit() {
            // given
            badgeAchievementRepository.save(new BadgeAchievement(badge1, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge2, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge3, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge4, savedMember));

            // when
            List<OnlyBadge> byMemberIdWithBadgeList =
                    badgeAchievementRepository.findByMemberIdWithBadgeOrderByAchievedAtLimit(savedMember.memberId(), 3);

            // then
            assertEquals(3, byMemberIdWithBadgeList.size());

            OffsetDateTime achievedAt1 = byMemberIdWithBadgeList.get(0).createdAt();
            OffsetDateTime achievedAt2 = byMemberIdWithBadgeList.get(1).createdAt();
            OffsetDateTime achievedAt3 = byMemberIdWithBadgeList.get(2).createdAt();

            assertTrue(achievedAt1.isAfter(achievedAt2));
            assertTrue(achievedAt2.isAfter(achievedAt3));
        }

        @Test
        @DisplayName("나의 뱃지 목록을 조회한다: 뱃지 타입순, 최신 획득한 순으로 데이터를 리턴한다.")
        void findBadgesList() {
            // given
            badgeAchievementRepository.save(new BadgeAchievement(badge1, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge2, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge3, savedMember));
            badgeAchievementRepository.save(new BadgeAchievement(badge4, savedMember));

            // when
            List<OnlyBadge> achievedBadges =
                    badgeAchievementRepository.findByMemberIdOrderByBadgeTypeAndAchievedAt(savedMember.memberId());

            // then
            OnlyBadge achieved1 = achievedBadges.get(0);
            OnlyBadge achieved2 = achievedBadges.get(1);
            OnlyBadge achieved3 = achievedBadges.get(2);
            OnlyBadge achieved4 = achievedBadges.get(3);

            assertTrue(achieved1.badge().type().compareTo(achieved3.badge().type()) > 0);
            assertTrue(achieved1.createdAt().isAfter(achieved2.createdAt()));
            assertTrue(achieved3.createdAt().isAfter(achieved4.createdAt()));
        }
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
