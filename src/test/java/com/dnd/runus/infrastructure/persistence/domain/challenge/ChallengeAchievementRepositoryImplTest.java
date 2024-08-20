package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengePercentageValues;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ChallengeAchievementRepositoryImplTest {

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @DisplayName("사용자 챌린지 성취 저장: 퍼센테이지 바 있는 경우")
    @Test
    void saveAchievementWithPercentage() {
        // given
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        RunningRecord runningRecord = runningRecordRepository.save(new RunningRecord(
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
        long challengeId = 1L;
        ChallengeAchievementRecord record =
                new ChallengeAchievementRecord(true, true, new ChallengePercentageValues(3000, 0, 1000));
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(savedMember, runningRecord, challengeId, record);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.record().successStatus());
        assertTrue(saved.record().hasPercentage());
        assertNotNull(saved.record().percentageValues());

        ChallengePercentageValues percentageValues = saved.record().percentageValues();
        assertThat(percentageValues.myValue()).isEqualTo(3000);
        assertThat(percentageValues.startValue()).isEqualTo(0);
        assertThat(percentageValues.endValue()).isEqualTo(1000);
        assertThat(percentageValues.percentage()).isEqualTo(100);
    }

    @DisplayName("사용자 챌린지 성취 저장: 퍼센테이지 바 없는 경우")
    @Test
    void saveAchievementWithoutPercentage() {
        // given
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        long challengeId = 1L;
        RunningRecord runningRecord = runningRecordRepository.save(new RunningRecord(
                1L,
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
        ChallengeAchievementRecord record = new ChallengeAchievementRecord(true, false, null);
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(savedMember, runningRecord, challengeId, record);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.record().successStatus());
        assertFalse(saved.record().hasPercentage());
        assertNull(saved.record().percentageValues());
    }

    @DisplayName("사용자 챌린지 기록 조회 : runningid로 조회")
    @Test
    void findByRunningId() {
        // given
        Member member = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        RunningRecord runningRecord = runningRecordRepository.save(new RunningRecord(
                1L,
                member,
                3000,
                Duration.ofMinutes(30),
                1,
                new Pace(5, 10),
                LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(ZoneOffset.of("+9")),
                LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(ZoneOffset.of("+9")),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        ChallengeAchievementRecord record =
                new ChallengeAchievementRecord(true, true, new ChallengePercentageValues(3000, 0, 1000));
        challengeAchievementRepository.save(new ChallengeAchievement(member, runningRecord, 1L, record));

        // when
        ChallengeAchievement challengeAchievement = challengeAchievementRepository
                .findByMemberIdAndRunningRecordId(member.memberId(), runningRecord.runningId())
                .orElse(null);

        // then
        assertNotNull(challengeAchievement);
    }
}
