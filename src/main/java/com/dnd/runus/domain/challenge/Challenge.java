package com.dnd.runus.domain.challenge;

import com.dnd.runus.domain.running.RunningRecord;

import java.util.Map;

public record Challenge(
        long challengeId,
        String name,
        String expectedTime,
        String imageUrl,
        ChallengeType challengeType,
        Map<ChallengeGoalType, Integer> goalValuesByType) {

    public boolean isDefeatYesterdayChallenge() {
        return this.challengeType == ChallengeType.DEFEAT_YESTERDAY;
    }

    /**
     * 어제의 기록과 관련된 챌린지들의 목표값 변환
     *
     * @param yesterdayRunningRecord 어제의 러닝 기록
     */
    public void convertGoalValuesWithYesterdayRecord(RunningRecord yesterdayRunningRecord) {
        for (Map.Entry<ChallengeGoalType, Integer> goalValueByType : goalValuesByType.entrySet()) {
            Integer newGoalValue = goalValueByType
                    .getKey()
                    .calculateGoalValueWithPastRecords(yesterdayRunningRecord, goalValueByType.getValue());
            goalValueByType.setValue(newGoalValue);
        }
    }

    /**
     * ChallengeGoalType별 목표 값들로 성공 여부 확인 후, 성취 결과 반환 메서드
     * ChallengeGoalType 별 목표 값이 여러개일 경우, 우선 순위의 결과 값을 반환한다.
     * 단, 성공 여부는 전체 목표값을 성공했을 경우에만 성공이다.
     * 퍼센테이지바가 없는 ChallengeGoalType이 하나라도 있는 경우 성공 여부만 기록한다.
     *
     * @param runningRecord 챌린지를 한 러닝 기록
     * @return 챌린지 성취 기록
     */
    public ChallengeAchievementRecord toAchievementRecordBy(RunningRecord runningRecord) {

        int priority = 0;
        boolean allSuccessful = true;
        boolean hasPercentage = true;
        ChallengePercentageValues percentageValues = null;

        for (Map.Entry<ChallengeGoalType, Integer> goalValueByType : goalValuesByType.entrySet()) {
            ChallengeGoalType goalType = goalValueByType.getKey();
            ChallengeAchievementRecord achievementRecordByGoalType =
                    goalType.makeChallengeAchievementRecordByGoalType(runningRecord, goalValueByType.getValue());

            // 하나라도 성공못한 기록이 있으면
            if (!achievementRecordByGoalType.successStatus()) {
                allSuccessful = false;
            }
            // 하나라도 hasPercentage가 false면
            if (!achievementRecordByGoalType.hasPercentage()) {
                hasPercentage = false;
            }

            if (goalType.getPriority() > priority) {
                // Percentage 바가 없으면 성공 여부만
                if (!hasPercentage) {
                    percentageValues = null;
                } else {
                    percentageValues = achievementRecordByGoalType.percentageValues();
                }
            }
        }

        return new ChallengeAchievementRecord(allSuccessful, hasPercentage, percentageValues);
    }
}
