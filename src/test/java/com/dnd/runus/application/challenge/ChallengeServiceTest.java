package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengePercentageValues;
import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievementDto;
import com.dnd.runus.domain.challenge.*;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private OffsetDateTime todayMidnight;

    @BeforeEach
    public void setUp() {
        todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
    }

    @DisplayName("어제 기록이 있는경우 챌린지 리스트 조회 : 챌린지 name에 '어제'값이 포함한 값이 있어야함")
    @Test
    void getChallengesWithYesterdayRecords() {
        // given
        Member member = new Member(MemberRole.USER, "nickname1");

        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(true);
        given(challengeRepository.getChallenges(true))
                .willReturn(Arrays.asList(
                        new Challenge(1L, "어제보다 1km더 뛰기", "8분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(2L, "어제보다 5분 더 뛰기", "5분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(
                                3L, "어제보다 평균 페이스 10초 빠르게", "0분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(4L, "오늘 5km 뛰기", "0분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(5L, "오늘 30분 동안 뛰기", "30분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(6L, "1km 6분안에 뛰기", "30분", "imageUrl", ChallengeType.DISTANCE_IN_TIME, null)));

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertTrue(challenges.stream().anyMatch(c -> c.name().contains("어제")));
    }

    @DisplayName("어제 기록이 없는 경우 챌린지 리스트 조회 : 챌린지 name에 '어제'값이 포함한 값이 없어야함")
    @Test
    void getChallengesWithoutYesterdayRecords() {
        // given
        Member member = new Member(MemberRole.USER, "nickname1");
        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(false);
        given(challengeRepository.getChallenges(false))
                .willReturn(Arrays.asList(
                        new Challenge(4L, "오늘 5km 뛰기", "0분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(5L, "오늘 30분 동안 뛰기", "30분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(6L, "1km 6분안에 뛰기", "30분", "imageUrl", ChallengeType.DISTANCE_IN_TIME, null)));

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertTrue(challenges.stream().noneMatch(c -> c.name().contains("어제")));
    }

    @DisplayName("챌린지 저장 성공: 어제의 기록을 이기는 챌린지")
    @Test
    void saveByYesterdayRecordChallenge_Success() {
        // given;
        int goalDistance = 1000;
        Member member = new Member(MemberRole.USER, "nickname1");

        Challenge challenge = new Challenge(
                1L,
                "어제보다 1km 더 달리기",
                "25분",
                "url",
                ChallengeType.DEFEAT_YESTERDAY,
                List.of(new ChallengeCondition(1, 1, GoalType.DISTANCE, ComparisonType.GREATER, goalDistance)));

        OffsetDateTime yesterdayMidnight = todayMidnight.minusDays(1);

        RunningRecord runningRecord = new RunningRecord(
                2,
                member,
                3000,
                Duration.ofMinutes(30),
                1,
                new Pace(5, 10),
                todayMidnight.plusHours(6),
                todayMidnight.plusHours(6).plusMinutes(30),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        RunningRecord yesterdayrunningRecord = new RunningRecord(
                1,
                member,
                500,
                Duration.ofMinutes(30),
                1,
                new Pace(5, 10),
                yesterdayMidnight.plusHours(6),
                yesterdayMidnight.plusHours(6).plusMinutes(30),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        given(challengeRepository.findById(challenge.challengeId())).willReturn(Optional.of(challenge));
        given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                        member.memberId(), yesterdayMidnight, todayMidnight))
                .willReturn(Collections.singletonList(yesterdayrunningRecord));

        ChallengePercentageValues percentageValues = new ChallengePercentageValues(
                runningRecord.distanceMeter(), 0, yesterdayrunningRecord.distanceMeter() + goalDistance);
        ChallengeAchievement expected = new ChallengeAchievement(
                runningRecord, challenge.challengeId(), new ChallengeAchievementRecord(true, true, percentageValues));
        given(challengeAchievementRepository.save(expected)).willReturn(expected);

        // when
        ChallengeAchievementDto response = challengeService.save(runningRecord, challenge.challengeId());

        // then
        assertNotNull(response);
        assertTrue(response.successStatus());
    }

    @DisplayName("챌린지 저장 성공: 오늘의 챌린지")
    @Test
    void saveByNotYesterdayRecordChallenge_Success() {
        // given;
        int goalDistance = 1000;
        Member member = new Member(MemberRole.USER, "nickname1");

        Challenge challenge = new Challenge(
                1L,
                "1km 더 달리기",
                "25분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1, 1, GoalType.DISTANCE, ComparisonType.GREATER, goalDistance)));

        RunningRecord runningRecord = new RunningRecord(
                2,
                member,
                3000,
                Duration.ofMinutes(30),
                1,
                new Pace(5, 10),
                todayMidnight.plusHours(6),
                todayMidnight.plusHours(6).plusMinutes(30),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        given(challengeRepository.findById(challenge.challengeId())).willReturn(Optional.of(challenge));

        ChallengePercentageValues percentageValues =
                new ChallengePercentageValues(runningRecord.distanceMeter(), 0, goalDistance);
        ChallengeAchievement expected = new ChallengeAchievement(
                runningRecord, challenge.challengeId(), new ChallengeAchievementRecord(true, true, percentageValues));
        given(challengeAchievementRepository.save(expected)).willReturn(expected);

        // when
        ChallengeAchievementDto response = challengeService.save(runningRecord, challenge.challengeId());

        // then
        assertNotNull(response);
        assertTrue(response.successStatus());
    }

    @DisplayName("챌린지 기록 조회 : 챌린지 기록이 있는 경우")
    @Test
    void findChallengeAchievement() {
        // given
        long runningId = 1L;
        long challengeId = 1L;
        Challenge challenge = new Challenge(
                challengeId,
                "1km 달리기",
                "25분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, challengeId, GoalType.DISTANCE, ComparisonType.GREATER, 1000)));
        RunningRecord runningRecord = new RunningRecord(
                runningId,
                new Member(MemberRole.USER, "nickname"),
                3000,
                Duration.ofMinutes(30),
                1,
                new Pace(5, 10),
                todayMidnight,
                todayMidnight.plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        ChallengePercentageValues percentageValues = new ChallengePercentageValues(1000, 1000, 0);
        ChallengeAchievement achievement = new ChallengeAchievement(
                runningRecord, challengeId, new ChallengeAchievementRecord(true, percentageValues));

        given(challengeAchievementRepository.findByRunningRecordId(runningId)).willReturn(Optional.of(achievement));
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));

        // when
        ChallengeAchievementDto response = challengeService.findChallengeAchievementBy(runningId);

        // then
        assertNotNull(response);
        assertTrue(response.successStatus());
    }

    @DisplayName("챌린지 기록 조회 : 챌린지 기록이 없는 경우 null 리턴")
    @Test
    void findChallengeAchievement_returnNull() {
        // given
        long runningId = 1L;

        given(challengeAchievementRepository.findByRunningRecordId(runningId)).willReturn(Optional.empty());

        // when
        ChallengeAchievementDto response = challengeService.findChallengeAchievementBy(runningId);

        // then
        assertNull(response);
    }
}
