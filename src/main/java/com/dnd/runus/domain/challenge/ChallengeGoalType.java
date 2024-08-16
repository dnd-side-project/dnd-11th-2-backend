package com.dnd.runus.domain.challenge;

import com.dnd.runus.domain.running.RunningRecord;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

/**
 * GoalType은 오늘의 챌린지의 목표 타겟에 대한 챌린지 타입이다.
 * GoalType은 아래와 같은 타입을 나타낸다.
 * <p> {@code DISTANCE} : 챌린지 목표 타겟이 거리인 타입
 * <p> {@code TIME} : 챌린지 목표 타겟이 시간인 타입
 * <p> {@code PACE} : 챌린지 목표 타겟이 페이스인 타입
 *
 * <p>챌린지 타입에 따라 챌린지 결과 기록을 다르게 계산합니다.
 */
@RequiredArgsConstructor
public enum ChallengeGoalType {
    DISTANCE(
            1,
            (yesterdayRecord, goalValue) -> yesterdayRecord.distanceMeter() + goalValue,
            (runningRecord, goalValue) -> {
                boolean iSuccess = runningRecord.distanceMeter() >= goalValue;
                ChallengePercentageValues challengePercentageValues =
                        new ChallengePercentageValues(runningRecord.distanceMeter(), 0, goalValue);
                return new ChallengeAchievementRecord(iSuccess, challengePercentageValues);
            }),
    TIME(
            1,
            (yesterdayRecord, goalValue) ->
                    Math.toIntExact(yesterdayRecord.duration().toSeconds() + goalValue),
            (runningRecord, goalValue) -> {
                int startHour = runningRecord.startAt().getHour();
                int startMinute = runningRecord.startAt().getMinute();
                int startSecondAt = (startHour * 60 * 60) + (startMinute * 60);
                int goalSecondAt = startSecondAt + goalValue;
                int myDuration = Math.toIntExact(runningRecord.duration().toSeconds());

                ChallengePercentageValues challengePercentageValues =
                        new ChallengePercentageValues(myDuration, startSecondAt, goalSecondAt);

                boolean isSuccess = runningRecord.duration().toSeconds() >= goalValue;

                return new ChallengeAchievementRecord(isSuccess, challengePercentageValues);
            }),
    PACE(
            2,
            (yesterdayRecord, goalValue) -> yesterdayRecord.averagePace().toSeconds() - goalValue,
            (runningRecord, goalValue) -> {
                boolean isSuccess = runningRecord.averagePace().toSeconds() <= goalValue;
                return new ChallengeAchievementRecord(isSuccess);
            });

    private final int descendingPriorityForResult; // 챌린지 우선 순위(목표값이 2개 이상인 챌린지일 경우, 사용자에게 보여주는 결과에 대한 우선 순위)
    private final BiFunction<RunningRecord, Integer, Integer> calGoalValueExpression;
    private final BiFunction<RunningRecord, Integer, ChallengeAchievementRecord>
            makeChallengeAchievementRecordExpression;

    /**
     * 목표 값 계산 메서드
     * 어제 기록을 이기는 챌린지일 경우
     * 챌린지 목표 값과 이전 기록의 값으로 사용자의 챌린지 목표 값을 계산합니다.
     *
     * @param yesterdayRecord 어제의 러닝 기록
     * @param goalValue       챌린지의 목표 값
     * @return 사용자의 챌린지 목표 값
     */
    public Integer calculateGoalValueWithPastRecords(RunningRecord yesterdayRecord, Integer goalValue) {
        return calGoalValueExpression.apply(yesterdayRecord, goalValue);
    }

    /**
     * 챌린지 성취 결과 기록을 만드는 메서드
     *
     * @param runningRecord 해당 챌린지를 한 사용자 러닝 기록
     * @param goalValue     사용자의 챌린지 목표 값
     * @return ChallengeAchievementRecord
     */
    public ChallengeAchievementRecord makeChallengeAchievementRecordByGoalType(
            RunningRecord runningRecord, Integer goalValue) {
        return makeChallengeAchievementRecordExpression.apply(runningRecord, goalValue);
    }

    public int getPriority() {
        return descendingPriorityForResult;
    }
}
