package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementPercentageEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
class ChallengeAchievementPercentageRepositoryImplTest {

    @Autowired
    private ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private EntityManager em;

    private List<ChallengeAchievement> savedChallengeAchievements;

    @BeforeEach
    void setUp() {
        // ChallengeAchievement, running
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));

        List<RunningRecord> savedRunningRecords = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            savedRunningRecords.add(runningRecordRepository.save(new RunningRecord(
                    0,
                    savedMember,
                    1,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now(),
                    List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO)));
        }

        Challenge challenge = new Challenge(1, "name", 60, "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY);

        savedChallengeAchievements = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ChallengeAchievement saved = challengeAchievementRepository.save(
                    new ChallengeAchievement(challenge, savedRunningRecords.get(i), true));
            savedChallengeAchievements.add(saved);
        }
    }

    @DisplayName("save percentage")
    @Test
    void savePercentage() {
        // when
        PercentageValues savedPercentage = challengeAchievementPercentageRepository.save(
                new ChallengeAchievementRecord(savedChallengeAchievements.get(0), new PercentageValues(0, 0, 0)));

        // then
        assertNotNull(savedPercentage);
    }

    @DisplayName("delete percentage by challengeAchievementId list")
    @Test
    void deletePercentageByChallengeAchievementIdList() {
        // given
        for (int i = 0; i < 2; i++) {
            challengeAchievementPercentageRepository.save(
                    new ChallengeAchievementRecord(savedChallengeAchievements.get(i), new PercentageValues(0, 0, 0)));
        }
        List<Long> idList = savedChallengeAchievements.stream()
                .map(ChallengeAchievement::challengeAchievementId)
                .toList();

        // when
        challengeAchievementPercentageRepository.deleteByChallengeAchievementIds(idList);

        // then
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<ChallengeAchievementPercentageEntity> query =
                criteriaBuilder.createQuery(ChallengeAchievementPercentageEntity.class);
        List<ChallengeAchievementPercentageEntity> selectAll = em.createQuery(
                        query.select(query.from(ChallengeAchievementPercentageEntity.class)))
                .getResultList();

        assertTrue(selectAll.isEmpty());
    }
}
