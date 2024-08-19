package com.dnd.runus.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengePercentageValues;
import com.dnd.runus.domain.running.RunningRecord;

import java.util.List;

public record Challenge(
        long challengeId,
        String name,
        String expectedTime,
        String imageUrl,
        ChallengeType challengeType,
        List<ChallengeCondition> conditions) {

    public boolean isDefeatYesterdayChallenge() {
        return this.challengeType == ChallengeType.DEFEAT_YESTERDAY;
    }

    /**
     * 러닝 기록으로 챌린지 성취 기록을 반환한다.
     * conditions(조건들)에 해당하는 모든 성취 조건을 만족해야 성공이다.
     * conditions(조건들)에 하나라도 퍼센테이지 값 여부가 false인경우 해당 챌린지 성취 기록에는 퍼센테이지 값이 존재하지 않는다.
     *
     * @param runningRecord 챌린지를 한 러닝 기록
     * @return 챌린지 성취 기록
     */
    public ChallengeAchievementRecord getAchievementRecord(RunningRecord runningRecord) {
        boolean allSuccess = true;
        boolean allHasPercentage = true;
        ChallengePercentageValues percentageValues = null;

        for (ChallengeCondition condition : conditions) {
            boolean success = condition.isAchieved(condition.goalType().getActualValue(runningRecord));

            allSuccess &= success;
            if (!condition.hasPercentage()) allHasPercentage = false;

            if (allHasPercentage) {
                percentageValues = new ChallengePercentageValues(
                        condition.goalType().getActualValue(runningRecord), 0, condition.requiredValue());
            } else {
                percentageValues = null;
            }
        }
        return new ChallengeAchievementRecord(allSuccess, allHasPercentage, percentageValues);
    }
}
