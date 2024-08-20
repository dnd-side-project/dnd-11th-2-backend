package com.dnd.runus.domain.challenge;

import com.dnd.runus.annotation.IntegrationTest;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengePercentageValues;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
class ChallengeTest {

    private final ZoneOffset defaultZoneOffset = ZoneOffset.of("+9");

    @DisplayName("챌린지가 어제의 기록을 이기는 챌린지인지 확인")
    @Test
    public void isDefeatChallenge() {
        // given
        Challenge defeatYesterdayChallenge = new Challenge(
                1L,
                "defeatYesterday",
                "0분",
                "url",
                ChallengeType.DEFEAT_YESTERDAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, 500)));
        Challenge todyaChallenge = new Challenge(
                1L,
                "otherChallenge",
                "0분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, 500)));
        Challenge otherChallenge = new Challenge(
                1L,
                "otherChallenge",
                "0분",
                "url",
                ChallengeType.DISTANCE_IN_TIME,
                List.of(
                        new ChallengeCondition(1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, 500),
                        new ChallengeCondition(1L, 1L, ChallengeGoalType.PACE, ComparisonType.GREATER, 6 * 60)));

        // when & then
        assertTrue(defeatYesterdayChallenge.isDefeatYesterdayChallenge());
        assertFalse(todyaChallenge.isDefeatYesterdayChallenge());
        assertFalse(otherChallenge.isDefeatYesterdayChallenge());
    }

    @DisplayName("챌린지 목표 값 계산(어제의 기록이 있는경우): 어제 기록 +(-) 챌린지 목표값")
    @Test
    public void calculateGoalValue() {
        // given
        int goalChallengeDis = 500;
        int goalChallengeTime = 10 * 60;
        Pace goalChallengePace = new Pace(0, 10);

        Challenge challengeDataForDis = new Challenge(
                1L,
                "어제보다 500m더 달리기",
                "4분",
                "url",
                ChallengeType.DEFEAT_YESTERDAY,
                List.of(new ChallengeCondition(
                        1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalChallengeDis)));

        Challenge challengeDataForTime = new Challenge(
                2L,
                "어제보다 10분 더 달리기",
                "10분",
                "url",
                ChallengeType.DEFEAT_YESTERDAY,
                List.of(new ChallengeCondition(
                        1L, 2L, ChallengeGoalType.TIME, ComparisonType.GREATER, goalChallengeTime)));
        Challenge challengeDataForPace = new Challenge(
                3L,
                "어제보다 10초 더빠른 페이스로 달리기",
                "0분",
                "url",
                ChallengeType.DEFEAT_YESTERDAY,
                List.of(new ChallengeCondition(
                        3L, 2L, ChallengeGoalType.PACE, ComparisonType.LESS, goalChallengePace.toSeconds())));

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
                .conditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));
        challengeDataForPace
                .conditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));
        challengeDataForTime
                .conditions()
                .forEach(condition ->
                        condition.registerComparisonValue(condition.goalType().getActualValue(yesterdayRecord)));

        // then
        int expectedDis = yesterdayRecord.distanceMeter() + goalChallengeDis;
        int expectedTime = Math.toIntExact(yesterdayRecord.duration().toSeconds()) + goalChallengeTime;
        int expectedPace = yesterdayRecord.averagePace().toSeconds() - goalChallengePace.toSeconds();

        Integer updatedDis = challengeDataForDis.conditions().get(0).comparisonValue();
        Integer updatedTime = challengeDataForTime.conditions().get(0).comparisonValue();
        Integer updatedPace = challengeDataForPace.conditions().get(0).comparisonValue();

        assertThat(updatedDis).isEqualTo(expectedDis);
        assertThat(updatedTime).isEqualTo(expectedTime);
        assertThat(updatedPace).isEqualTo(expectedPace);
    }

    @DisplayName("챌린지 결과 확인: 거리, 챌린지 성공")
    @Test
    public void getChallengeRecordWithDistanceChallengeForSuccess() {
        // given
        int goalDistance = 3000;
        Challenge challengeDataForDis = new Challenge(
                1L,
                "3km 달리기",
                "25분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(
                        1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance)));
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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertTrue(challengeAchievementRecord.successStatus());
        assertTrue(challengeAchievementRecord.hasPercentage());

        ChallengePercentageValues percentageValues = challengeAchievementRecord.percentageValues();
        assertThat(percentageValues.startValue()).isEqualTo(0);
        assertThat(percentageValues.endValue()).isEqualTo(goalDistance);
        assertThat(percentageValues.myValue()).isEqualTo(runningRecord.distanceMeter());
        assertThat(percentageValues.percentage()).isEqualTo(100);
    }

    @DisplayName("챌린지 결과 확인: 거리, 챌린지 실패")
    @Test
    public void getChallengeRecordWithDistanceChallengeForFail() {
        // given
        int goalDistance = 3000;
        Challenge challengeDataForDis = new Challenge(
                1L,
                "3km 달리기",
                "25분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(
                        1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance)));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertTrue(challengeAchievementRecord.hasPercentage());

        ChallengePercentageValues percentageValues = challengeAchievementRecord.percentageValues();
        assertThat(percentageValues.startValue()).isEqualTo(0);
        assertThat(percentageValues.endValue()).isEqualTo(3000);
        assertThat(percentageValues.myValue()).isEqualTo(runningRecord.distanceMeter());
        assertThat(percentageValues.percentage()).isEqualTo(99);
    }

    @DisplayName("챌린지 결과 확인: 시간, 챌린지 성공")
    @Test
    public void getChallengeRecordWithTimeForSuccess() {
        // given
        int goalTime = 60 * 60 + 30 * 60; // 1시간 30분, 5,400
        OffsetDateTime startAt = LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset);
        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.TIME, ComparisonType.GREATER, goalTime)));
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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertTrue(challengeAchievementRecord.successStatus());
        assertTrue(challengeAchievementRecord.hasPercentage());

        ChallengePercentageValues percentageValues = challengeAchievementRecord.percentageValues();
        int expectedStartValue = 0;
        int expectedEndValue = expectedStartValue + goalTime;

        assertThat(percentageValues.startValue()).isEqualTo(expectedStartValue);
        assertThat(percentageValues.endValue()).isEqualTo(expectedEndValue);
        assertThat(percentageValues.myValue())
                .isEqualTo(runningRecord.duration().toSeconds());
        assertThat(percentageValues.percentage()).isEqualTo(100);
    }

    @DisplayName("챌린지 결과 확인: 시간, 챌린지 실패")
    @Test
    public void getChallengeRecordWithTimeForFail() {
        // given
        int goalTime = 60 * 60 + 30 * 60; // 1시간 30분
        OffsetDateTime startAt = LocalDateTime.of(2021, 1, 1, 13, 10, 0).atOffset(defaultZoneOffset);
        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.TIME, ComparisonType.GREATER, goalTime)));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertTrue(challengeAchievementRecord.hasPercentage());

        ChallengePercentageValues percentageValues = challengeAchievementRecord.percentageValues();
        int expectedStartValue = 0;
        int expectedEndValue = expectedStartValue + goalTime;

        assertThat(percentageValues.startValue()).isEqualTo(expectedStartValue);
        assertThat(percentageValues.endValue()).isEqualTo(expectedEndValue);
        assertThat(percentageValues.myValue())
                .isEqualTo(runningRecord.duration().toSeconds());
        assertThat(percentageValues.percentage()).isEqualTo(98);
    }

    @DisplayName("챌린지 결과 확인: 페이스, 챌린지 성공")
    @Test
    public void getChallengeRecordWithPaceForSuccess() {
        // given
        Pace pace = new Pace(6, 0); // 패이스 목표 600
        Challenge challengeDataForDis = new Challenge(
                1L,
                "평균페이스 600",
                "0분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, pace.toSeconds())));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertTrue(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    @DisplayName("챌린지 결과 확인: 페이스, 챌린지 실패")
    @Test
    public void getChallengeRecordWithPaceForFail() {
        // given
        Pace pace = new Pace(6, 0); // 패이스 목표 600
        Challenge challengeDataForDis = new Challenge(
                1L,
                "평균페이스 600",
                "1시간 30분",
                "url",
                ChallengeType.TODAY,
                List.of(new ChallengeCondition(1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, pace.toSeconds())));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    @DisplayName("챌린지 결과 확인: 복합(거리+페이스), 챌린지 성공")
    @Test
    void getChallengeRecordWithDistanceAndPaceForSuccess() {
        // given
        int goalDistance = 1000;
        Pace goalPace = new Pace(6, 0);

        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.DISTANCE_IN_TIME,
                List.of(
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance),
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, goalPace.toSeconds())));
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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertTrue(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    @DisplayName("챌린지 결과 확인: 복합(거리+페이스), 챌린지 실패(전체 목표)")
    @Test
    void getChallengeRecordWithDistanceAndPaceForFailAll() {
        // given
        int goalDistance = 1000;
        Pace goalPace = new Pace(6, 0);

        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.DISTANCE_IN_TIME,
                List.of(
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance),
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, goalPace.toSeconds())));
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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    @DisplayName("챌린지 결과 확인: 복합(거리+페이스), 챌린지 실패(거리 실패로)")
    @Test
    void getChallengeRecordWithDistanceAndPaceForFailDis() {
        // given
        int goalDistance = 1000;
        Pace goalPace = new Pace(6, 0);

        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.DISTANCE_IN_TIME,
                List.of(
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance),
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, goalPace.toSeconds())));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    @DisplayName("챌린지 결과 확인: 복합(거리+페이스), 챌린지 실패(페이스 실패로)")
    @Test
    void getChallengeRecordWithDistanceAndPaceForFailPace() {
        // given
        int goalDistance = 1000;
        Pace goalPace = new Pace(6, 0);

        Challenge challengeDataForDis = new Challenge(
                1L,
                "1시간 30분 달리기",
                "1시간 30분",
                "url",
                ChallengeType.DISTANCE_IN_TIME,
                List.of(
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.DISTANCE, ComparisonType.GREATER, goalDistance),
                        new ChallengeCondition(
                                1L, 1L, ChallengeGoalType.PACE, ComparisonType.LESS, goalPace.toSeconds())));

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
        ChallengeAchievementRecord challengeAchievementRecord = challengeDataForDis.getAchievementRecord(runningRecord);

        // then
        assertFalse(challengeAchievementRecord.successStatus());
        assertFalse(challengeAchievementRecord.hasPercentage());
        assertNull(challengeAchievementRecord.percentageValues());
    }

    private ChallengeAchievementRecord getAchievementRecord(
            List<ChallengeCondition> conditions, RunningRecord runningRecord) {
        boolean allSuccess = true;
        ChallengePercentageValues percentageValues = null;

        for (ChallengeCondition condition : conditions) {
            boolean success = condition.isAchieved(condition.goalType().getActualValue(runningRecord));

            allSuccess &= success;

            if (condition.hasPercentage()) {
                percentageValues = new ChallengePercentageValues(
                        condition.goalType().getActualValue(runningRecord), 0, condition.requiredValue());
            }
        }

        return new ChallengeAchievementRecord(allSuccess, percentageValues);
    }
}
