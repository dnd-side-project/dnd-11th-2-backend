package com.dnd.runus.application.running;

import com.dnd.runus.application.running.dto.RunningResultDto;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.*;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsForAddDto;
import com.dnd.runus.presentation.v1.running.dto.WeeklyRunningRatingDto;
import com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequestV1;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordWeeklySummaryType;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordAddResultResponseV1;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordMonthlySummaryResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordWeeklySummaryResponse;
import com.dnd.runus.presentation.v2.running.dto.RouteDtoV2;
import com.dnd.runus.presentation.v2.running.dto.request.RunningRecordRequestV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class RunningRecordServiceTest {
    private RunningRecordService runningRecordService;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Mock
    private ChallengeAchievementPercentageRepository percentageValuesRepository;

    @Mock
    private GoalAchievementRepository goalAchievementRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final ZoneOffset defaultZoneOffset = ZoneOffset.of("+9");

    @BeforeEach
    void setUp() {
        runningRecordService = new RunningRecordService(
                runningRecordRepository,
                memberRepository,
                challengeRepository,
                challengeAchievementRepository,
                percentageValuesRepository,
                goalAchievementRepository,
                eventPublisher,
                defaultZoneOffset);
    }

    @Test
    @DisplayName("러닝 기록 조회 - 존재하는 러닝 기록 조회")
    void getRunningRecord() {
        // given
        long memberId = 1;
        long runningRecordId = 1;
        Member member = new Member(memberId, MemberRole.USER, "nickname1", OffsetDateTime.now(), OffsetDateTime.now());
        RunningRecord runningRecord = new RunningRecord(
                1L,
                member,
                10_000,
                Duration.ofSeconds(10_000),
                500,
                new Pace(5, 30),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(0, 0, 0), new CoordinatePoint(0, 0, 0)),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD);

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.of(runningRecord));

        // when
        RunningResultDto result = runningRecordService.getRunningRecord(memberId, runningRecordId);

        // then
        assertEquals(runningRecordId, result.runningRecord().runningId());
        assertEquals(runningRecord.emoji(), result.runningRecord().emoji());
    }

    @Test
    @DisplayName("러닝 기록 조회 - 존재하지 않는 러닝 기록 조회한다면 NotFoundException을 던진다.")
    void getRunningRecord_not_found() {
        // given
        long memberId = 1;
        long runningRecordId = 1;

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> runningRecordService.getRunningRecord(memberId, runningRecordId));
    }

    @Test
    @DisplayName("러닝 기록 조회 - 존재하지 않는 멤버의 러닝 기록 조회한다면 NotFoundException을 던진다.")
    void getRunningRecord_member_not_found() {
        // given
        long memberId = 1;
        long runningRecordId = 1;

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> runningRecordService.getRunningRecord(memberId, runningRecordId));
    }

    @Test
    @DisplayName("러닝 기록 조회 - Challenge 모드의 러닝 기록 조회")
    void getRunningRecord_challenge() {
        // given
        long memberId = 1;
        long runningRecordId = 1;
        Challenge challenge = new Challenge(1L, "challenge", "image", true, ChallengeType.TODAY);
        Member member = new Member(memberId, MemberRole.USER, "nickname1", OffsetDateTime.now(), OffsetDateTime.now());
        RunningRecord runningRecord = new RunningRecord(
                1L,
                member,
                10_000,
                Duration.ofSeconds(10_000),
                500,
                new Pace(5, 30),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(0, 0, 0), new CoordinatePoint(0, 0, 0)),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD);

        ChallengeAchievement.Status challengeAchievementStatus = new ChallengeAchievement.Status(1L, challenge, true);

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.of(runningRecord));
        given(challengeAchievementRepository.findByRunningRecordId(runningRecordId))
                .willReturn(Optional.of(challengeAchievementStatus));
        given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                .willReturn(Optional.of(new ChallengeWithCondition(
                        challenge,
                        List.of(new ChallengeCondition(
                                GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_000)))));

        // when
        RunningResultDto result = runningRecordService.getRunningRecord(memberId, runningRecordId);

        // then
        assertEquals(runningRecordId, result.runningRecord().runningId());
        assertEquals(runningRecord.emoji(), result.runningRecord().emoji());
        assertNotNull(result.challengeAchievement());
        assertEquals(RunningAchievementMode.CHALLENGE, result.runningAchievementMode());
        assertEquals(
                challengeAchievementStatus.challenge().name(),
                result.challengeAchievement().challenge().name());
        assertEquals(1, result.percentage());
    }

    @Test
    @DisplayName("Challenge 모드의 러닝 기록 조회 : DISTANCE_IN_TIME 타입일 경우 퍼센테이지 null 리턴 ")
    void getRunningRecord_challenge_DISTANCE_IN_TIME_Type() {
        // given
        long memberId = 1;
        long runningRecordId = 1;
        Challenge challenge = new Challenge(1L, "challenge", "image", true, ChallengeType.DISTANCE_IN_TIME);
        Member member = new Member(memberId, MemberRole.USER, "nickname1", OffsetDateTime.now(), OffsetDateTime.now());
        RunningRecord runningRecord = new RunningRecord(
                1L,
                member,
                10_000,
                Duration.ofSeconds(10_000),
                500,
                new Pace(5, 30),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(0, 0, 0), new CoordinatePoint(0, 0, 0)),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD);

        ChallengeAchievement.Status challengeAchievementStatus = new ChallengeAchievement.Status(1L, challenge, true);

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.of(runningRecord));
        given(challengeAchievementRepository.findByRunningRecordId(runningRecordId))
                .willReturn(Optional.of(challengeAchievementStatus));
        given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                .willReturn(Optional.of(new ChallengeWithCondition(
                        challenge,
                        List.of(new ChallengeCondition(
                                GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_000)))));

        // when
        RunningResultDto result = runningRecordService.getRunningRecord(memberId, runningRecordId);

        // then
        assertEquals(runningRecordId, result.runningRecord().runningId());
        assertEquals(runningRecord.emoji(), result.runningRecord().emoji());
        assertNotNull(result.challengeAchievement());
        assertEquals(RunningAchievementMode.CHALLENGE, result.runningAchievementMode());
        assertEquals(
                challengeAchievementStatus.challenge().name(),
                result.challengeAchievement().challenge().name());
        assertNull(result.percentage());
    }

    @Test
    @DisplayName("Challenge 모드의 러닝 기록 조회 : 퍼센테이지를 표시 할 수 없는 챌린지가 하나라도 있을 경우 퍼센테이지 null 리턴 ")
    void getRunningRecord_challenge_hasNotPercentage() {
        // given
        long memberId = 1;
        long runningRecordId = 1;
        Challenge challenge = new Challenge(1L, "challenge", "image", true, ChallengeType.TODAY);
        Member member = new Member(memberId, MemberRole.USER, "nickname1", OffsetDateTime.now(), OffsetDateTime.now());
        RunningRecord runningRecord = new RunningRecord(
                1L,
                member,
                10_000,
                Duration.ofSeconds(10_000),
                500,
                new Pace(5, 30),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(0, 0, 0), new CoordinatePoint(0, 0, 0)),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD);

        ChallengeAchievement.Status challengeAchievementStatus = new ChallengeAchievement.Status(1L, challenge, true);

        given(runningRecordRepository.findById(runningRecordId)).willReturn(Optional.of(runningRecord));
        given(challengeAchievementRepository.findByRunningRecordId(runningRecordId))
                .willReturn(Optional.of(challengeAchievementStatus));
        given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                .willReturn(Optional.of(new ChallengeWithCondition(
                        challenge,
                        List.of(
                                new ChallengeCondition(
                                        GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_000),
                                new ChallengeCondition(
                                        GoalMetricType.PACE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1_000)))));

        // when
        RunningResultDto result = runningRecordService.getRunningRecord(memberId, runningRecordId);

        // then
        assertEquals(runningRecordId, result.runningRecord().runningId());
        assertEquals(runningRecord.emoji(), result.runningRecord().emoji());
        assertNotNull(result.challengeAchievement());
        assertEquals(RunningAchievementMode.CHALLENGE, result.runningAchievementMode());
        assertEquals(
                challengeAchievementStatus.challenge().name(),
                result.challengeAchievement().challenge().name());
        assertNull(result.percentage());
    }

    @Test
    @DisplayName("CHALLENGE 모드의 러닝 기록 추가 요청시, challengeId에 해당하는 챌린지가 있을 경우, 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord_challenge() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                1L,
                null,
                null,
                RunningAchievementMode.CHALLENGE,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(10_100), 10_000, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        ChallengeWithCondition challengeWithCondition = new ChallengeWithCondition(
                new Challenge(1L, "challenge", "image", true, ChallengeType.TODAY),
                List.of(new ChallengeCondition(
                        GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 10_000)));
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(0L, challengeWithCondition.challenge(), expected, true);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);
        given(challengeRepository.findChallengeWithConditionsByChallengeId(1L))
                .willReturn(Optional.of(challengeWithCondition));
        given(challengeAchievementRepository.save(challengeAchievement)).willReturn(challengeAchievement);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());
    }

    @Test
    @DisplayName("GOAL 모드의 러닝 기록 추가 시,goalTime이 null이 아니고 목표값보다 실제값이 더 크다면, 성공한 goalAchievement와 함께 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord_goal_time_success() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                null,
                null,
                1200,
                RunningAchievementMode.GOAL,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(10_100), 10_000, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        GoalAchievement goalAchievement = new GoalAchievement(expected, GoalMetricType.TIME, 1200, true);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);
        given(goalAchievementRepository.save(goalAchievement)).willReturn(goalAchievement);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());

        assertTrue(response.goal().title().contains("분"));
        assertFalse(response.goal().title().contains("km"));

        assertTrue(response.goal().isSuccess());
    }

    @Test
    @DisplayName(
            "GOAL 모드의 러닝 기록 추가 시, goalTime이 null이 아니고 목표값보다 실제값이 더 낮다면, 실패한 goalAchievement와 함께 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord_goal_time_fail() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                null,
                null,
                20_000,
                RunningAchievementMode.GOAL,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(10_100), 10_000, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        GoalAchievement goalAchievement = new GoalAchievement(expected, GoalMetricType.TIME, 20_000, false);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);
        given(goalAchievementRepository.save(goalAchievement)).willReturn(goalAchievement);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());

        assertTrue(response.goal().title().contains("분"));
        assertFalse(response.goal().title().contains("km"));

        assertFalse(response.goal().isSuccess());
    }

    @Test
    @DisplayName(
            "GOAL 모드의 러닝 기록 추가 요청시, goalDistance가 null이 아니고 목표값보다 실제값이 더 높다면 성공한 goalAchievement와 함께 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord_goal_distance() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                null,
                5_000,
                null,
                RunningAchievementMode.GOAL,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(10_100), 10_000, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        GoalAchievement goalAchievement = new GoalAchievement(expected, GoalMetricType.DISTANCE, 5_000, true);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);
        given(goalAchievementRepository.save(goalAchievement)).willReturn(goalAchievement);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());

        assertTrue(response.goal().title().contains("km"));
        assertFalse(response.goal().title().contains("분"));

        assertTrue(response.goal().isSuccess());
    }

    @Test
    @DisplayName(
            "GOAL 모드의 러닝 기록 추가 요청시, goalDistance가 null이 아니고 목표값보다 실제값과 같다면 성공한 goalAchievement와 함께 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord_goal_distance_same_value() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                null,
                10_000,
                null,
                RunningAchievementMode.GOAL,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(10_100), 10_000, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        GoalAchievement goalAchievement = new GoalAchievement(expected, GoalMetricType.DISTANCE, 10_000, true);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);
        given(goalAchievementRepository.save(goalAchievement)).willReturn(goalAchievement);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());

        assertTrue(response.goal().title().contains("km"));
        assertFalse(response.goal().title().contains("분"));

        assertTrue(response.goal().isSuccess());
    }

    @Test
    @DisplayName("러닝의 페이스가 올바르게 계산되었는지 확인한다.")
    void addRunningRecord_check_cal_pace() {
        // given
        RunningRecordRequestV1 request = new RunningRecordRequestV1(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                "start location",
                "end location",
                RunningEmoji.VERY_GOOD,
                null,
                null,
                null,
                RunningAchievementMode.NORMAL,
                new RunningRecordMetricsForAddDto(Duration.ofSeconds(1_668), 3_280, 500.0));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = createRunningRecord(request, member);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);

        // when
        RunningRecordAddResultResponseV1 response = runningRecordService.addRunningRecordV1(1L, request);

        // then
        assertEquals(new Pace(8, 28), response.runningData().averagePace());
    }

    @Test
    @DisplayName("이번 달, 달린 키로수를 조회한다.")
    void getMonthlyRunningSummery() {
        // given
        long memberId = 1;
        OffsetDateTime fixedDate = ZonedDateTime.of(2021, 1, 15, 9, 0, 0, 0, ZoneId.of(SERVER_TIMEZONE))
                .toOffsetDateTime();
        try (MockedStatic<OffsetDateTime> mockedStatic = mockStatic(OffsetDateTime.class)) {
            mockedStatic
                    .when(() -> OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE)))
                    .thenReturn(fixedDate);

            given(runningRecordRepository.findTotalDistanceMeterByMemberIdWithRangeDate(eq(memberId), any(), any()))
                    .willReturn(45_780);

            // when
            RunningRecordMonthlySummaryResponse monthlyRunningSummery =
                    runningRecordService.getMonthlyRunningSummery(memberId);

            // then
            assertNotNull(monthlyRunningSummery);
            assertThat(monthlyRunningSummery.month()).isEqualTo(1);
            assertThat(monthlyRunningSummery.monthlyTotalMeter()).isEqualTo(45_780);
        }
    }

    @Test
    @DisplayName("활동 요약 조회(거리)")
    void getWeeklySummary_Distance() {
        // given
        long memberId = 1;
        RunningRecordWeeklySummaryType summaryType = RunningRecordWeeklySummaryType.DISTANCE;

        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int todayDayOfWeek = today.get(DAY_OF_WEEK);
        OffsetDateTime startWeekDate = today.minusDays(todayDayOfWeek - 1);
        OffsetDateTime nextOfEndWeekDate = startWeekDate.plusDays(7);

        OffsetDateTime runningDate = startWeekDate.plusDays(2);

        given(runningRecordRepository.findDailyDistancesMeterWithDateRange(memberId, startWeekDate, nextOfEndWeekDate))
                .willReturn(List.of(new DailyRunningRecordSummary(runningDate.toLocalDate(), 3567)));

        given(runningRecordRepository.findAvgDistanceMeterByMemberIdWithDateRange(
                        memberId, startWeekDate.minusDays(7), nextOfEndWeekDate.minusDays(7)))
                .willReturn(800);

        // when
        RunningRecordWeeklySummaryResponse response = runningRecordService.getWeeklySummary(memberId, summaryType);

        // then
        List<WeeklyRunningRatingDto> weeklyValues = response.weeklyValues();
        WeeklyRunningRatingDto weeklyValue = weeklyValues.get(runningDate.get(DAY_OF_WEEK) - 1);
        assertThat(weeklyValues.size()).isEqualTo(7);
        assertThat(weeklyValue.rating()).isEqualTo(3.567);
        assertThat(response.lastWeekAvgValue()).isEqualTo(0.8);
    }

    @Test
    @DisplayName("활동 요약 조회(시간)")
    void getWeeklySummary_Duration() {
        // given
        long memberId = 1;
        RunningRecordWeeklySummaryType summaryType = RunningRecordWeeklySummaryType.TIME;
        int runningDurationSec = 3600 + 30 * 60; // 1시간 30분
        double expectedRunningDurationHour = 1.5;

        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int todayDayOfWeek = today.get(DAY_OF_WEEK);
        OffsetDateTime startWeekDate = today.minusDays(todayDayOfWeek - 1);
        OffsetDateTime nextOfEndWeekDate = startWeekDate.plusDays(7);

        OffsetDateTime runningDate = startWeekDate.plusDays(2);

        given(runningRecordRepository.findDailyDurationsSecWithDateRange(memberId, startWeekDate, nextOfEndWeekDate))
                .willReturn(List.of(new DailyRunningRecordSummary(runningDate.toLocalDate(), runningDurationSec)));

        given(runningRecordRepository.findAvgDurationSecByMemberIdWithDateRange(
                        memberId, startWeekDate.minusDays(7), nextOfEndWeekDate.minusDays(7)))
                .willReturn(runningDurationSec);

        // when
        RunningRecordWeeklySummaryResponse response = runningRecordService.getWeeklySummary(memberId, summaryType);

        // then
        List<WeeklyRunningRatingDto> weeklyValues = response.weeklyValues();
        WeeklyRunningRatingDto weeklyValue = weeklyValues.get(runningDate.get(DAY_OF_WEEK) - 1);
        assertThat(weeklyValues.size()).isEqualTo(7);
        assertThat(weeklyValue.rating()).isEqualTo(expectedRunningDurationHour);
        assertThat(response.lastWeekAvgValue()).isEqualTo(expectedRunningDurationHour);
    }

    private RunningRecord createRunningRecord(RunningRecordRequestV1 request, Member member) {

        return RunningRecord.builder()
                .member(member)
                .startAt(request.startAt().atZone(defaultZoneOffset))
                .endAt(request.endAt().atZone(defaultZoneOffset))
                .emoji(request.emotion())
                .startLocation(request.startLocation())
                .endLocation(request.endLocation())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(Pace.from(
                        request.runningData().distanceMeter(),
                        request.runningData().runningTime()))
                .route(List.of(new CoordinatePoint(0, 0, 0), new CoordinatePoint(0, 0, 0)))
                .build();
    }

    @Nested
    @DisplayName("러닝 결과 저장 V2")
    class RunningRecordAddV2 {

        private RunningRecordRequestV2.RunningRecordMetrics runningRecordMetrics;

        @BeforeEach
        void beforeEach() {
            runningRecordMetrics = new RunningRecordRequestV2.RunningRecordMetrics(
                    Duration.ofSeconds(600),
                    1000,
                    500.0,
                    List.of(
                            new RouteDtoV2(new RouteDtoV2.Point(0, 0), new RouteDtoV2.Point(1, 1)),
                            new RouteDtoV2(new RouteDtoV2.Point(2, 2), new RouteDtoV2.Point(3, 3)),
                            new RouteDtoV2(new RouteDtoV2.Point(4, 4), new RouteDtoV2.Point(5, 5))));
        }

        @Test
        @DisplayName("러닝 결과 저장 : 루트가 순서대로 들어갔는지 확인")
        void addRunningRecordV2_CheckRoute() {
            // given
            Member member = new Member(MemberRole.USER, "nickname1");
            RunningRecordRequestV2 request = new RunningRecordRequestV2(
                    LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                    LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                    "start location",
                    "end location",
                    RunningEmoji.VERY_GOOD,
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.NORMAL,
                    null,
                    null,
                    runningRecordMetrics);

            RunningRecord expectedRecord = createRecordFrom(member, request);

            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.save(expectedRecord)).willReturn(expectedRecord);

            // when
            RunningResultDto response = runningRecordService.addRunningRecordV2(member.memberId(), request);

            // then
            assertEquals(
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.NORMAL,
                    response.runningAchievementMode());
            assertNull(response.challengeAchievement());
            assertNull(response.goalAchievement());
            assertNull(response.percentage());

            RunningRecord resultRunning = response.runningRecord();
            for (int i = 0; i < resultRunning.route().size(); i++) {
                assertEquals(i, resultRunning.route().get(i).longitude());
                assertEquals(i, resultRunning.route().get(i).latitude());
            }
        }

        @Test
        @DisplayName("러닝 모드가가 챌린지(어제보다 ~) : 기준이 되는 러닝 기록의 어제 러닝 기록이 없으면 NotFoundException를 발생한다.")
        void addRunningRecordV2_ChallengeMode_DefeatYesterday_RunningRecordNotFoundError() {
            // given
            Member member = new Member(MemberRole.USER, "nickname1");
            Challenge challenge =
                    new Challenge(1L, "어제보다 1km 더 달리기", 360, "image url", true, ChallengeType.DEFEAT_YESTERDAY);
            RunningRecordRequestV2 request = new RunningRecordRequestV2(
                    LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                    LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                    "start location",
                    "end location",
                    RunningEmoji.VERY_GOOD,
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE,
                    new RunningRecordRequestV2.ChallengeAchievedDto(challenge.challengeId(), true),
                    null,
                    runningRecordMetrics);
            RunningRecord expectedRecord = createRecordFrom(member, request);
            ChallengeAchievement expectedChallengeAchievement = new ChallengeAchievement(
                    challenge, expectedRecord, request.challengeValues().isSuccess());
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.save(expectedRecord)).willReturn(expectedRecord);
            given(challengeRepository.findById(request.challengeValues().challengeId()))
                    .willReturn(Optional.of(challenge));
            given(challengeAchievementRepository.save(expectedChallengeAchievement))
                    .willReturn(expectedChallengeAchievement);
            given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                    .willReturn(Optional.of(new ChallengeWithCondition(
                            challenge,
                            List.of(new ChallengeCondition(
                                    GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1000)))));
            OffsetDateTime runningDate = expectedRecord
                    .startAt()
                    .toLocalDate()
                    .atStartOfDay(expectedRecord.startAt().getZone())
                    .toOffsetDateTime();
            given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                            member.memberId(), runningDate.minusDays(1), runningDate))
                    .willReturn(List.of());

            // when, then
            assertThrows(
                    NotFoundException.class, () -> runningRecordService.addRunningRecordV2(member.memberId(), request));
        }

        @Test
        @DisplayName("러닝 모드가 챌린지(어제보다 ~km더 뛰기) : 챌린지 관련 값이 정상적으로 저장되었는지 확인한다.")
        void addRunningRecordV2_ChallengeMode_DefeatYesterday_Distance() {
            // given
            Member member = new Member(MemberRole.USER, "nickname1");
            Challenge challenge =
                    new Challenge(1L, "어제보다 1km 더 달리기", 360, "image url", true, ChallengeType.DEFEAT_YESTERDAY);
            RunningRecordRequestV2 request = new RunningRecordRequestV2(
                    LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                    LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                    "start location",
                    "end location",
                    RunningEmoji.VERY_GOOD,
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE,
                    new RunningRecordRequestV2.ChallengeAchievedDto(challenge.challengeId(), false),
                    null,
                    runningRecordMetrics);
            RunningRecord expectedRecord = createRecordFrom(member, request);
            ChallengeAchievement expectedChallengeAchievement = new ChallengeAchievement(
                    challenge, expectedRecord, request.challengeValues().isSuccess());
            OffsetDateTime runningDate = expectedRecord
                    .startAt()
                    .toLocalDate()
                    .atStartOfDay(expectedRecord.startAt().getZone())
                    .toOffsetDateTime();
            RunningRecord yesterdayRunning = RunningRecord.builder()
                    .member(member)
                    .startAt(expectedRecord.startAt().minusDays(1))
                    .endAt(expectedRecord.endAt().minusDays(1))
                    .emoji(expectedRecord.emoji())
                    .startLocation(expectedRecord.startLocation())
                    .endLocation(expectedRecord.endLocation())
                    .distanceMeter(expectedRecord.distanceMeter())
                    .duration(expectedRecord.duration())
                    .calorie(expectedRecord.calorie())
                    .averagePace(expectedRecord.averagePace())
                    .route(expectedRecord.route())
                    .build();
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.save(expectedRecord)).willReturn(expectedRecord);
            given(challengeRepository.findById(request.challengeValues().challengeId()))
                    .willReturn(Optional.of(challenge));
            given(challengeAchievementRepository.save(expectedChallengeAchievement))
                    .willReturn(expectedChallengeAchievement);
            given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                    .willReturn(Optional.of(new ChallengeWithCondition(
                            challenge,
                            List.of(new ChallengeCondition(
                                    GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1000)))));
            given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                            member.memberId(), runningDate.minusDays(1), runningDate))
                    .willReturn(List.of(yesterdayRunning));

            // when
            RunningResultDto result = runningRecordService.addRunningRecordV2(member.memberId(), request);

            // then
            assertEquals(
                    result.runningAchievementMode(),
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE);
            assertNotNull(result.challengeAchievement());
            assertNotNull(result.percentage());
            assertEquals(0.5, result.percentage());
        }

        @Test
        @DisplayName("러닝 모드가가 챌린지(어제보다 ~분 더 뛰기) : 챌린지 관련 값이 정상적으로 저장되었는지 확인한다.")
        void addRunningRecordV2_ChallengeMode_DefeatYesterday_Time() {
            // given
            Member member = new Member(MemberRole.USER, "nickname1");
            Challenge challenge =
                    new Challenge(1L, "어제보다 30분 더 달리기", 360, "image url", true, ChallengeType.DEFEAT_YESTERDAY);
            RunningRecordRequestV2 request = new RunningRecordRequestV2(
                    LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                    LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                    "start location",
                    "end location",
                    RunningEmoji.VERY_GOOD,
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE,
                    new RunningRecordRequestV2.ChallengeAchievedDto(challenge.challengeId(), false),
                    null,
                    runningRecordMetrics);
            RunningRecord expectedRecord = createRecordFrom(member, request);
            ChallengeAchievement expectedChallengeAchievement = new ChallengeAchievement(
                    challenge, expectedRecord, request.challengeValues().isSuccess());
            OffsetDateTime runningDate = expectedRecord
                    .startAt()
                    .toLocalDate()
                    .atStartOfDay(expectedRecord.startAt().getZone())
                    .toOffsetDateTime();
            RunningRecord yesterdayRunning = RunningRecord.builder()
                    .member(member)
                    .startAt(expectedRecord.startAt().minusDays(1))
                    .endAt(expectedRecord.endAt().minusDays(1))
                    .emoji(expectedRecord.emoji())
                    .startLocation(expectedRecord.startLocation())
                    .endLocation(expectedRecord.endLocation())
                    .distanceMeter(expectedRecord.distanceMeter())
                    .duration(expectedRecord.duration())
                    .calorie(expectedRecord.calorie())
                    .averagePace(expectedRecord.averagePace())
                    .route(expectedRecord.route())
                    .build();
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.save(expectedRecord)).willReturn(expectedRecord);
            given(challengeRepository.findById(request.challengeValues().challengeId()))
                    .willReturn(Optional.of(challenge));
            given(challengeAchievementRepository.save(expectedChallengeAchievement))
                    .willReturn(expectedChallengeAchievement);
            given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                    .willReturn(Optional.of(new ChallengeWithCondition(
                            challenge,
                            List.of(new ChallengeCondition(
                                    GoalMetricType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 1800)))));
            given(runningRecordRepository.findByMemberIdAndStartAtBetween(
                            member.memberId(), runningDate.minusDays(1), runningDate))
                    .willReturn(List.of(yesterdayRunning));

            // when
            RunningResultDto result = runningRecordService.addRunningRecordV2(member.memberId(), request);

            // then
            assertEquals(
                    result.runningAchievementMode(),
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE);
            assertNotNull(result.challengeAchievement());
            assertNotNull(result.percentage());
            assertEquals(0.25, result.percentage());
        }

        @Test
        @DisplayName("러닝 모드가가 챌린지이고 챌린지가 성공일 경우 퍼센티이지 값은 1를 리턴한다.")
        void addRunningRecordV2_ChallengeMode_CheckPercentage() {
            // given
            Member member = new Member(MemberRole.USER, "nickname1");
            Challenge challenge = new Challenge(1L, "500m 달리기", 360, "image url", true, ChallengeType.TODAY);
            RunningRecordRequestV2 request = new RunningRecordRequestV2(
                    LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                    LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                    "start location",
                    "end location",
                    RunningEmoji.VERY_GOOD,
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE,
                    new RunningRecordRequestV2.ChallengeAchievedDto(challenge.challengeId(), true),
                    null,
                    runningRecordMetrics);
            RunningRecord expectedRecord = createRecordFrom(member, request);
            ChallengeAchievement expectedChallengeAchievement = new ChallengeAchievement(
                    challenge, expectedRecord, request.challengeValues().isSuccess());
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.save(expectedRecord)).willReturn(expectedRecord);
            given(challengeRepository.findById(request.challengeValues().challengeId()))
                    .willReturn(Optional.of(challenge));
            given(challengeAchievementRepository.save(expectedChallengeAchievement))
                    .willReturn(expectedChallengeAchievement);
            given(challengeRepository.findChallengeWithConditionsByChallengeId(challenge.challengeId()))
                    .willReturn(Optional.of(new ChallengeWithCondition(
                            challenge,
                            List.of(new ChallengeCondition(
                                    GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 500)))));

            // when
            RunningResultDto result = runningRecordService.addRunningRecordV2(member.memberId(), request);

            // then
            assertEquals(
                    result.runningAchievementMode(),
                    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE);
            assertNotNull(result.challengeAchievement());
            assertNotNull(result.percentage());
            assertEquals(1, result.percentage());
        }

        private RunningRecord createRecordFrom(Member member, RunningRecordRequestV2 request) {
            List<CoordinatePoint> coordinatePoints = request.runningData().route().stream()
                    .flatMap(point -> Stream.of(
                            new CoordinatePoint(
                                    point.start().longitude(), point.start().latitude()),
                            new CoordinatePoint(
                                    point.end().longitude(), point.end().latitude())))
                    .collect(Collectors.toList());

            return RunningRecord.builder()
                    .member(member)
                    .startAt(request.startAt().atZone(defaultZoneOffset))
                    .endAt(request.endAt().atZone(defaultZoneOffset))
                    .emoji(request.emotion())
                    .startLocation(request.startLocation())
                    .endLocation(request.endLocation())
                    .distanceMeter(request.runningData().distanceMeter())
                    .duration(request.runningData().runningTime())
                    .calorie(request.runningData().calorie())
                    .averagePace(Pace.from(
                            request.runningData().distanceMeter(),
                            request.runningData().runningTime()))
                    .route(coordinatePoints)
                    .build();
        }
    }
}
