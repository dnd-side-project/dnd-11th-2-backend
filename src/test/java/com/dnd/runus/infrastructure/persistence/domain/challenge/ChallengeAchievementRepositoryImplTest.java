package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ChallengeAchievementRepositoryImplTest {

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private EntityManager em;

    private List<RunningRecord> savedRunningRecords;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));

        RunningRecord runningRecord = new RunningRecord(
                0,
                savedMember,
                1,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        savedRunningRecords = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            savedRunningRecords.add(runningRecordRepository.save(runningRecord));
        }
        challenge = new Challenge(1, "name", 60, "imageUrl", ChallengeType.DEFEAT_YESTERDAY);
    }

    @DisplayName("ChallengeAchievement 저장시, 성공여부가 true인지 확인")
    @Test
    void saveAchievement() {
        // given
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(challenge, savedRunningRecords.get(0), true);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.isSuccess());
    }

    @DisplayName("러닝 레코드 리스트로 ChallengeAchievement의 id 찾기")
    @Test
    void findIdsByRunningRecordsTest() {
        // given
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ids.add(challengeAchievementRepository
                    .save(new ChallengeAchievement(challenge, savedRunningRecords.get(i), true))
                    .ChallengeAchievementId());
        }

        // then
        List<Long> resultIds = challengeAchievementRepository.findIdsByRunningRecords(savedRunningRecords);

        // when
        assertNotNull(resultIds);
        assertThat(resultIds.size()).isEqualTo(ids.size());
        assertTrue(resultIds.containsAll(ids));
    }

    @DisplayName("Challenge achievement 삭제 : id 리스트로")
    @Test
    void deleteAchievementByIdsTest() {
        // given
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ids.add(challengeAchievementRepository
                    .save(new ChallengeAchievement(challenge, savedRunningRecords.get(i), true))
                    .ChallengeAchievementId());
        }

        // when
        challengeAchievementRepository.deleteByIds(ids);

        // then
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<ChallengeAchievementEntity> query = criteriaBuilder.createQuery(ChallengeAchievementEntity.class);
        List<ChallengeAchievementEntity> selectAll = em.createQuery(
                        query.select(query.from(ChallengeAchievementEntity.class)))
                .getResultList();

        assertThat(selectAll.size()).isEqualTo(0);
    }
}
