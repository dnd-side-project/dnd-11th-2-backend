package com.dnd.runus.infrastructure.persistence.domain.oauth;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.oauth.OauthRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementPercentageEntity;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeEntity;
import com.dnd.runus.infrastructure.persistence.jpa.level.entity.LevelEntity;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@RepositoryTest
class OauthRepositoryImplTest {

    @Autowired
    private OauthRepository oauthRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private ScaleAchievementRepository scaleAchievementRepository;

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private GoalAchievementRepository goalAchievementRepository;

    @Autowired
    private ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;

    private long levelId;
    private long scaleId;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        // set up level, challenge, scale
        em.persist(LevelEntity.from(new Level(0, 0, 1000000, "img")));
        em.persist(ScaleEntity.from(new Scale(0, "scale1", 1_000_000, 1, "서울(한국)", "도쿄(일본)")));
        em.persist(ChallengeEntity.from(new Challenge(0, "challenge", 0, "img", ChallengeType.TODAY)));
        em.flush();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<LevelEntity> levelQuery = criteriaBuilder.createQuery(LevelEntity.class);
        Root<LevelEntity> levelFrom = levelQuery.from(LevelEntity.class);
        levelQuery.select(levelFrom);
        levelId = em.createQuery(levelQuery).getResultList().get(0).getId();

        CriteriaQuery<ScaleEntity> scaleQuery = criteriaBuilder.createQuery(ScaleEntity.class);
        Root<ScaleEntity> scaleFrom = scaleQuery.from(ScaleEntity.class);
        scaleQuery.select(scaleFrom);
        scaleId = em.createQuery(scaleQuery).getResultList().get(0).getId();

        CriteriaQuery<ChallengeEntity> challengeQuery = criteriaBuilder.createQuery(ChallengeEntity.class);
        Root<ChallengeEntity> challengeFrom = challengeQuery.from(ChallengeEntity.class);
        challengeQuery.select(challengeFrom);
        challenge = em.createQuery(challengeQuery).getResultList().get(0).toDomain();
    }

    @DisplayName("회원 탈퇴 시 회원의 모든 데이터를 삭제 한다.")
    @Test
    void withdraw() {
        // given
        // 1. member, socialProfile 저장
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        SocialProfile savedSocialProfile =
                socialProfileRepository.save(new SocialProfile(0, savedMember, SocialType.APPLE, "oauthId", "email"));
        // 2. member_level 저장
        memberLevelRepository.save(new MemberLevel(0, savedMember, levelId, 0));

        // 3. running record 저장
        RunningRecord savedRunningRecord = runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                1_100_000,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        // 4. scale achievement 저장
        scaleAchievementRepository.saveAll(
                List.of(new ScaleAchievement(savedMember, new Scale(scaleId), OffsetDateTime.now())));

        // 5. challenge achievement, goal achievement 저장
        ChallengeAchievement savedChallengeAc =
                challengeAchievementRepository.save(new ChallengeAchievement(challenge, savedRunningRecord, true));
        goalAchievementRepository.save(new GoalAchievement(savedRunningRecord, GoalMetricType.DISTANCE, 1000, true));

        // 6. challenge achievement percentage 저장
        challengeAchievementPercentageRepository.save(
                new ChallengeAchievementRecord(savedChallengeAc, new PercentageValues(0, 0, 0)));

        // when
        oauthRepository.deleteAllDataAboutMember(savedMember.memberId());

        // then
        // 1. member, socialProfile 확인
        assertFalse(memberRepository.findById(savedMember.memberId()).isPresent());
        assertFalse(socialProfileRepository
                .findById(savedSocialProfile.socialProfileId())
                .isPresent());

        // running record 확인
        assertFalse(
                runningRecordRepository.findById(savedRunningRecord.runningId()).isPresent());

        // scale achievement 확인
        assertNull(scaleAchievementRepository
                .findScaleAchievementLogs(savedMember.memberId())
                .get(0)
                .achievedDate());

        // goal achievement 확인
        assertFalse(goalAchievementRepository
                .findByRunningRecordId(savedRunningRecord.runningId())
                .isPresent());

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        // member_level 확인
        CriteriaQuery<MemberLevelEntity> memberLevelQuery = criteriaBuilder.createQuery(MemberLevelEntity.class);
        List<MemberLevelEntity> memberLevelSelect = em.createQuery(
                        memberLevelQuery.select(memberLevelQuery.from(MemberLevelEntity.class)))
                .getResultList();
        assertThat(memberLevelSelect.size()).isEqualTo(0);

        // running record 확인
        CriteriaQuery<RunningRecordEntity> runningQuery = criteriaBuilder.createQuery(RunningRecordEntity.class);
        List<RunningRecordEntity> runningSelect = em.createQuery(
                        runningQuery.select(runningQuery.from(RunningRecordEntity.class)))
                .getResultList();
        assertThat(runningSelect.size()).isEqualTo(0);

        // challenge achievement percentage 확인
        CriteriaQuery<ChallengeAchievementPercentageEntity> percentageQuery =
                criteriaBuilder.createQuery(ChallengeAchievementPercentageEntity.class);
        List<ChallengeAchievementPercentageEntity> percentageSelect = em.createQuery(
                        percentageQuery.select(percentageQuery.from(ChallengeAchievementPercentageEntity.class)))
                .getResultList();
        assertThat(percentageSelect.size()).isEqualTo(0);
    }
}
