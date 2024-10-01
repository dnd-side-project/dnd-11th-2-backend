package com.dnd.runus.infrastructure.persistence.domain.badge;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.badge.BadgeRepository;
import com.dnd.runus.domain.badge.BadgeWithAchieveStatusAndAchievedAt;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.badge.entity.BadgeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@RepositoryTest
class BadgeRepositoryTest {
    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private BadgeAchievementRepository badgeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Badge badge1;
    private Badge badge2;

    @BeforeEach
    void setUp() {

        // insert Badge test data
        em.persist(BadgeEntity.from(new Badge(0, "badge1", "description1", "imageUrl", BadgeType.STREAK, 0)));
        em.persist(BadgeEntity.from(new Badge(0, "badge2", "description2", "imageUrl", BadgeType.STREAK, 0)));
        em.flush();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<BadgeEntity> query = criteriaBuilder.createQuery(BadgeEntity.class);
        List<BadgeEntity> selectAll =
                em.createQuery(query.select(query.from(BadgeEntity.class))).getResultList();

        for (BadgeEntity badgeEntity : selectAll) {
            if (badgeEntity.getName().equals("badge1")) {
                badge1 = badgeEntity.toDomain();
            } else {
                badge2 = badgeEntity.toDomain();
            }
        }
    }

    @DisplayName("모든 배지 리스트를 조회 한다. 사용자가 해당 배지를 성취했을 경우 isAchieved는 true를 반환한다.")
    @Test
    void findAllBadges() {
        // given
        Member member = memberRepository.save(new Member(MemberRole.USER, "member1"));
        badgeAchievementRepository.save(new BadgeAchievement(badge1, member));

        // when
        List<BadgeWithAchieveStatusAndAchievedAt> allBadges =
                badgeRepository.findAllBadgesWithAchieveStatusByMemberId(member.memberId());

        // then
        assertThat(allBadges.size()).isEqualTo(2);
        assertThat(allBadges)
                .extracting(o -> o.badge().badgeId(), BadgeWithAchieveStatusAndAchievedAt::isAchieved)
                .containsExactlyInAnyOrder(tuple(badge1.badgeId(), true), tuple(badge2.badgeId(), false));
    }
}
