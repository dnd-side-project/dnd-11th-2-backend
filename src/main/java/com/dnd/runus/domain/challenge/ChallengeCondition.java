package com.dnd.runus.domain.challenge;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
public class ChallengeCondition {

    private final long challengeConditionId;
    private final long challengeId;
    private final ChallengeGoalType goalType;
    private final ComparisonType comparisonType;
    private final int goalValue; // 목표 값(챌린지가 지정한)

    private int comparisonValue; // 비교할 값(챌린지 성공 유무확인을 위한 비교할 값)

    public ChallengeCondition(
            long challengeConditionId,
            long challengeId,
            ChallengeGoalType goalType,
            ComparisonType comparisonType,
            int goalValue) {
        this.challengeConditionId = challengeConditionId;
        this.challengeId = challengeId;
        this.goalType = goalType;
        this.comparisonType = comparisonType;
        this.goalValue = goalValue;
        this.comparisonValue = goalValue;
    }

    public boolean isAchieved(int currentValue) {
        return comparisonType == ComparisonType.GREATER
                ? currentValue >= comparisonValue
                : currentValue <= comparisonValue;
    }

    public boolean hasPercentage() {
        return goalType.hasPercentage();
    }

    public void registerComparisonValue(int comparisonValue) {
        if (comparisonType == ComparisonType.GREATER) {
            this.comparisonValue += comparisonValue;
        } else {
            this.comparisonValue = comparisonValue - this.comparisonValue;
        }
    }

    public int requiredValue() {
        return comparisonValue;
    }
}
