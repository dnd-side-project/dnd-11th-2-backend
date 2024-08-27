package com.dnd.runus.infrastructure.persistence.domain.challenge;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

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

    private RunningRecord runningRecord;

    @BeforeEach
    void setUp() {
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        runningRecord = runningRecordRepository.save(new RunningRecord(
                1,
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
                RunningEmoji.SOSO));
    }

    @DisplayName("사용자 챌린지 성취 저장")
    @Test
    void saveAchievement() {
        // given
        long challengeId = 1L;
        ChallengeAchievement challengeAchievement = new ChallengeAchievement(challengeId, runningRecord, true);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.isSuccess());
    }

    @DisplayName("사용자 챌린지 기록 조회 : runningid로 조회")
    @Test
    void findByRunningId() {
        // given
        long challengeId = 1L;
        challengeAchievementRepository.save(new ChallengeAchievement(challengeId, runningRecord, true));

        // when
        ChallengeAchievement challengeAchievement = challengeAchievementRepository
                .findByRunningId(runningRecord.runningId())
                .orElse(null);

        // then
        assertNotNull(challengeAchievement);
    }
}
