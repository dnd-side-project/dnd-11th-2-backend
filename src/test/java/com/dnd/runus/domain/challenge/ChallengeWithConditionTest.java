package com.dnd.runus.domain.challenge;

import com.dnd.runus.annotation.IntegrationTest;
import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.dto.PercentageValues;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ChallengeWithConditionTest {

    private final ZoneOffset defaultZoneOffset = ZoneOffset.of("+9");

    @DisplayName("챌린지 예상 시간 Format 확인: 1시간 30분")
    @Test
    public void validExpectedTimeFormatToHHMM() {
        Challenge challenge = new Challenge(1, "1시간 30분 달리기", (3600 + 30 * 60), "imageUrl", ChallengeType.TODAY);

        assertThat(challenge.expectedTime()).isEqualTo("1시간 30분");
    }

    @DisplayName("챌린지 예상 시간 Format 확인: 1시간")
    @Test
    public void validExpectedTimeFormatToHH() {
        Challenge challenge = new Challenge(1, "1시간 달리기", 3600, "imageUrl", ChallengeType.TODAY);

        assertThat(challenge.expectedTime()).isEqualTo("1시간");
    }

    @DisplayName("챌린지 예상 시간 Format 확인: 30분")
    @Test
    public void validExpectedTimeFormatToMM() {
        Challenge challenge = new Challenge(1, "30분 달리기", (30 * 60), "imageUrl", ChallengeType.TODAY);

        assertThat(challenge.expectedTime()).isEqualTo("30분");
    }

    @DisplayName("챌린지 예상 시간 Format 확인: 0분")
    @Test
    public void validExpectedTimeFormatToZero() {
        Challenge challenge = new Challenge(1, "평균페이스 600", 0, "imageUrl", ChallengeType.TODAY);

        assertThat(challenge.expectedTime()).isEqualTo("0분");
    }

    @DisplayName("챌린지 목표 값 계산(어제의 기록이 있는경우): 어제 기록 +(-) 챌린지 목표값")
    @Test
    public void calculateGoalValue() {
        // given
        int goalChallengeDis = 500;
        int goalChallengeTime = 10 * 60;
        Pace goalChallengePace = new Pace(0, 10);
        ChallengeWithCondition challengeDataForDis = new ChallengeWithCondition(
                new Challenge(1L, "어제보다 500m더 달리기", "4분", "url", ChallengeType.DEFEAT_YESTERDAY),
                List.of(new ChallengeCondition(
                        GoalType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, goalChallengeDis)));
        ChallengeWithCondition challengeDataForTime = new ChallengeWithCondition(
                new Challenge(2L, "어제보다 10분 더 달리기", "10분", "url", ChallengeType.DEFEAT_YESTERDAY),
                List.of(new ChallengeCondition(
                        GoalType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, goalChallengeTime)));
        ChallengeWithCondition challengeDataForPace = new ChallengeWithCondition(
                new Challenge(3L, "어제보다 10초 더빠른 페이스로 달리기", "0분", "url", ChallengeType.DEFEAT_YESTERDAY),
                List.of(new ChallengeCondition(
                        GoalType.PACE, ComparisonType.LESS_THAN_OR_EQUAL_TO, goalChallengePace.toSeconds())));
        RunningRecord yesterdayRecord = new RunningRecord(
                0,
                new Member(MemberRole.USER, "nickname"),
                1000,
                Duration.ofHours(1).plusMinutes(30),
                1,
                new Pace(5, 10),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO);

        // when
        challengeDataForDis
                .challengeConditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));
        challengeDataForPace
                .challengeConditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));
        challengeDataForTime
                .challengeConditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));
        // then
        int expectedDis = yesterdayRecord.distanceMeter() + goalChallengeDis;
        int expectedTime = Math.toIntExact(yesterdayRecord.duration().toSeconds()) + goalChallengeTime;
        int expectedPace = yesterdayRecord.averagePace().toSeconds() - goalChallengePace.toSeconds();
        Integer updatedDis = challengeDataForDis.challengeConditions().get(0).comparisonValue();
        Integer updatedTime = challengeDataForTime.challengeConditions().get(0).comparisonValue();
        Integer updatedPace = challengeDataForPace.challengeConditions().get(0).comparisonValue();
        assertThat(updatedDis).isEqualTo(expectedDis);
        assertThat(updatedTime).isEqualTo(expectedTime);
        assertThat(updatedPace).isEqualTo(expectedPace);
    }

    @Nested
    @DisplayName("챌린지 결과 확인: 거리")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetChallengeRecordWithDistanceTest {

        private final int goalDistance = 3000;
        private ChallengeWithCondition challengeDataForDis;

        @BeforeEach
        void setUp() {
            challengeDataForDis = new ChallengeWithCondition(
                    new Challenge(1L, "3km 달리기", "25분", "url", ChallengeType.TODAY),
                    List.of(new ChallengeCondition(
                            GoalType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, goalDistance)));
        }

        @DisplayName("챌린지 성공")
        @Test
        public void SuccessChallenge() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofMinutes(30),
                    1,
                    new Pace(5, 10),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForDis);

            // then
            assertTrue(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertTrue(challengeAchievementRecord.hasPercentage());
            PercentageValues percentageValues = challengeAchievementRecord.percentageValues();
            assertThat(percentageValues.startValue()).isEqualTo(0);
            assertThat(percentageValues.endValue()).isEqualTo(goalDistance);
            assertThat(percentageValues.achievementValue()).isEqualTo(runningRecord.distanceMeter());
        }

        @DisplayName("챌린지 실패")
        @Test
        public void FailChallenge() {

            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    2999,
                    Duration.ofMinutes(30),
                    1,
                    new Pace(5, 10),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForDis);

            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertTrue(challengeAchievementRecord.hasPercentage());
            PercentageValues percentageValues = challengeAchievementRecord.percentageValues();
            assertThat(percentageValues.startValue()).isEqualTo(0);
            assertThat(percentageValues.endValue()).isEqualTo(3000);
            assertThat(percentageValues.achievementValue()).isEqualTo(runningRecord.distanceMeter());
        }
    }

    @Nested
    @DisplayName("챌린지 결과 확인: 시간")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetChallengeRecordWithTimeTest {

        private final int goalTime = 60 * 60 + 30 * 60; // 1시간 30분, 5,400
        OffsetDateTime startAt = LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset);
        private ChallengeWithCondition challengeDataForTime;

        @BeforeEach
        void setUp() {
            challengeDataForTime = new ChallengeWithCondition(
                    new Challenge(1L, "1시간 30분 달리기", "1시간 30분", "url", ChallengeType.TODAY),
                    List.of(new ChallengeCondition(GoalType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, goalTime)));
        }

        @DisplayName("챌린지 성공")
        @Test
        public void getChallengeRecordWithTimeForSuccess() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofHours(1).plusMinutes(30),
                    1,
                    new Pace(5, 10),
                    startAt,
                    startAt.plusHours(1).plusMinutes(30),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForTime);
            // then
            assertTrue(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertTrue(challengeAchievementRecord.hasPercentage());
            PercentageValues percentageValues = challengeAchievementRecord.percentageValues();
            int expectedStartValue = 0;
            int expectedEndValue = expectedStartValue + goalTime;
            assertThat(percentageValues.startValue()).isEqualTo(expectedStartValue);
            assertThat(percentageValues.endValue()).isEqualTo(expectedEndValue);
            assertThat(percentageValues.achievementValue())
                    .isEqualTo(runningRecord.duration().toSeconds());
        }

        @DisplayName(" 챌린지 실패")
        @Test
        public void getChallengeRecordWithTimeForFail() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(5, 10),
                    startAt,
                    startAt.plusHours(1).plusMinutes(29),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForTime);

            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertTrue(challengeAchievementRecord.hasPercentage());

            PercentageValues percentageValues = challengeAchievementRecord.percentageValues();
            int expectedStartValue = 0;
            int expectedEndValue = expectedStartValue + goalTime;
            assertThat(percentageValues.startValue()).isEqualTo(expectedStartValue);
            assertThat(percentageValues.endValue()).isEqualTo(expectedEndValue);
            assertThat(percentageValues.achievementValue())
                    .isEqualTo(runningRecord.duration().toSeconds());
        }
    }

    @Nested
    @DisplayName("챌린지 결과 확인: 페이스")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetChallengeRecordWithPaceTest {

        private final Pace pace = new Pace(6, 0); // 패이스 목표 600
        private ChallengeWithCondition challengeDataForPace;

        @BeforeEach
        void setUp() {
            challengeDataForPace = new ChallengeWithCondition(
                    new Challenge(1L, "평균페이스 600", "0분", "url", ChallengeType.TODAY),
                    List.of(new ChallengeCondition(
                            GoalType.PACE, ComparisonType.LESS_THAN_OR_EQUAL_TO, pace.toSeconds())));
        }

        @DisplayName("챌린지 성공")
        @Test
        public void getChallengeRecordWithPaceForSuccess() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 0),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPace);
            // then
            assertTrue(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }

        @DisplayName("챌린지 실패")
        @Test
        public void getChallengeRecordWithPaceForFail() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 1),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPace);
            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }
    }

    @Nested
    @DisplayName("챌린지 결과 확인: 복합(거리+페이스)")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetChallengeRecordWithDistanceAndPaceTest {

        private final Pace goalPace = new Pace(6, 0); // 패이스 목표 600
        private final int goalDistance = 1000;
        private ChallengeWithCondition challengeDataForPaceAndDis;

        @BeforeEach
        void setUp() {
            challengeDataForPaceAndDis = new ChallengeWithCondition(
                    new Challenge(1L, "1시간 30분 달리기", "1시간 30분", "url", ChallengeType.DISTANCE_IN_TIME),
                    List.of(
                            new ChallengeCondition(
                                    GoalType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, goalDistance),
                            new ChallengeCondition(
                                    GoalType.PACE, ComparisonType.LESS_THAN_OR_EQUAL_TO, goalPace.toSeconds())));
        }

        @DisplayName("챌린지 성공")
        @Test
        void getChallengeRecordWithDistanceAndPaceForSuccess() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    3000,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 0),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPaceAndDis);

            // then
            assertTrue(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }

        @DisplayName("챌린지 실패(전체 목표 실패)")
        @Test
        void getChallengeRecordWithDistanceAndPaceForFailAll() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    999,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 1),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPaceAndDis);

            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }

        @DisplayName("챌린지 실패(거리 실패)")
        @Test
        void getChallengeRecordWithDistanceAndPaceForFailDis() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    999,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 0),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPaceAndDis);
            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }

        @DisplayName("챌린지 실패(페이스 실패)")
        @Test
        void getChallengeRecordWithDistanceAndPaceForFailPace() {
            // given
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    new Member(MemberRole.USER, "nickname"),
                    1000,
                    Duration.ofHours(1).plusMinutes(29),
                    1,
                    new Pace(6, 1),
                    LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset),
                    LocalDateTime.of(2021, 1, 1, 13, 40, 30).atOffset(defaultZoneOffset),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);

            // when
            ChallengeAchievementRecord challengeAchievementRecord =
                    getChallengeAchievement(runningRecord, challengeDataForPaceAndDis);
            // then
            assertFalse(challengeAchievementRecord.challengeAchievement().isSuccess());
            assertFalse(challengeAchievementRecord.hasPercentage());
            assertNull(challengeAchievementRecord.percentageValues());
        }
    }

    private ChallengeAchievementRecord getChallengeAchievement(
            RunningRecord runningRecord, ChallengeWithCondition challengeDataWithConditions) {
        boolean allSuccess = true;
        boolean allHasPercentage = true;
        PercentageValues percentageValues = null;

        for (ChallengeCondition condition : challengeDataWithConditions.challengeConditions()) {
            boolean success = condition.isAchieved(condition.goalType().getActualValue(runningRecord));

            allSuccess &= success;
            if (!condition.hasPercentage()) {
                allHasPercentage = false;
            }

            if (allHasPercentage) {
                percentageValues = new PercentageValues(
                        condition.goalType().getActualValue(runningRecord), 0, condition.requiredValue());
            } else {
                percentageValues = null;
            }
        }

        ChallengeAchievement challengeAchievement = new ChallengeAchievement(
                challengeDataWithConditions.challengeInfo().challengeId(), runningRecord, allSuccess);

        return new ChallengeAchievementRecord(challengeAchievement, percentageValues);
    }
}
