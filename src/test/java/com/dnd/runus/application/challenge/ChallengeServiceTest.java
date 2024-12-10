package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.*;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
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
import java.time.ZonedDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private OffsetDateTime todayMidnight;
    private Member member;

    private ChallengeWithCondition challengeWithCondition1;
    private ChallengeWithCondition challengeWithCondition2;
    private ChallengeWithCondition challengeWithCondition3;
    private ChallengeWithCondition challengeWithCondition4;

    @BeforeEach
    void setUp() {
        todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        member = new Member(MemberRole.USER, "nickname");

        challengeWithCondition1 = new ChallengeWithCondition(
                new Challenge(1L, "어제보다 1km더 뛰기", "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY),
                List.of(new ChallengeCondition(
                        GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_000)));

        challengeWithCondition2 = new ChallengeWithCondition(
                new Challenge(2L, "어제보다 5분 더 뛰기", "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY),
                List.of(new ChallengeCondition(GoalMetricType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 300)));

        challengeWithCondition3 = new ChallengeWithCondition(
                new Challenge(3L, "오늘 5km 뛰기", "imageUrl", true, ChallengeType.TODAY),
                List.of(new ChallengeCondition(
                        GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 5_000)));

        challengeWithCondition4 = new ChallengeWithCondition(
                new Challenge(4L, "오늘 30분 뛰기", "imageUrl", true, ChallengeType.TODAY),
                List.of(new ChallengeCondition(GoalMetricType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_800)));
    }

    @DisplayName("챌린지 리스트 조회 : 챌린지 리스트 크기가 2이어야함")
    @Test
    void getChallenges_size_is_2() {
        // given
        given(challengeRepository.findAllActiveChallengesWithConditions())
                .willReturn(List.of(
                        challengeWithCondition1,
                        challengeWithCondition2,
                        challengeWithCondition3,
                        challengeWithCondition4));

        // when
        List<ChallengeWithCondition> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertThat(challenges.size()).isEqualTo(2);
    }

    @DisplayName("어제 기록이 있는경우 챌린지 리스트 조회 : 목표 값(comparisonValue)이 어제 기록 + 챌린지 목표 값이여햐함.")
    @Test
    void getChallengesWithYesterdayRecords_checkGoalValue() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);

        List<RunningRecord> runningRecords = List.of(
                new RunningRecord(
                        1L,
                        member,
                        3_000,
                        Duration.ofMinutes(21),
                        100,
                        new Pace(7, 0),
                        startDate,
                        startDate.plusMinutes(21),
                        List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                        "start location",
                        "end location",
                        RunningEmoji.SOSO),
                new RunningRecord(
                        2L,
                        member,
                        4_000,
                        Duration.ofMinutes(28),
                        100,
                        new Pace(7, 0),
                        startDate.plusMinutes(5),
                        startDate.plusMinutes(5).plusMinutes(28),
                        List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                        "start location",
                        "end location",
                        RunningEmoji.SOSO));
        given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(runningRecords);

        given(challengeRepository.findAllActiveChallengesWithConditions())
                .willReturn(List.of(challengeWithCondition1, challengeWithCondition2));

        // when
        List<ChallengeWithCondition> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertThat(challenges.size()).isEqualTo(2);

        ChallengeWithCondition challenge1 = challenges.stream()
                .filter(challenge -> challenge.challenge().challengeId()
                        == challengeWithCondition1.challenge().challengeId())
                .findFirst()
                .orElse(null);

        assertNotNull(challenge1);
        assertEquals(4_000, challenge1.conditions().getFirst().comparisonValue());

        ChallengeWithCondition challenge2 = challenges.stream()
                .filter(challenge -> challenge.challenge().challengeId()
                        == challengeWithCondition2.challenge().challengeId())
                .findFirst()
                .orElse(null);

        assertNotNull(challenge2);
        assertEquals((21 * 60) + 300, challenge2.conditions().getFirst().comparisonValue());
    }

    @DisplayName("어제 기록이 없는 경우 챌린지 리스트 조회 : 어제 기록과 관련되 첼린지가 있으면 안됨")
    @Test
    void getChallengesWithoutYesterdayRecords() {
        // given
        given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(List.of());

        given(challengeRepository.findAllActiveChallengesWithConditions())
                .willReturn(List.of(
                        challengeWithCondition1,
                        challengeWithCondition2,
                        challengeWithCondition3,
                        challengeWithCondition4));
        // when
        List<ChallengeWithCondition> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertThat(challenges.size()).isEqualTo(2);
        assertThat(challenges)
                .noneMatch(challengeWithCondition ->
                        challengeWithCondition.challenge().isDefeatYesterdayChallenge());
    }
}
