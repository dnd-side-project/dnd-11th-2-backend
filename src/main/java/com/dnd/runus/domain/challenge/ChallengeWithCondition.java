package com.dnd.runus.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
import com.dnd.runus.domain.running.RunningRecord;

import java.util.List;
import java.util.Optional;

public record ChallengeWithCondition(Challenge challenge, List<ChallengeCondition> conditions) {
    public ChallengeAchievementRecord getAchievementRecord(RunningRecord runningRecord) {
        Optional<ChallengeCondition> failedCondition = conditions.stream()
                .filter(condition ->
                        !condition.isAchieved(condition.goalMetricType().getActualValue(runningRecord)))
                .findFirst();

        boolean hasNoRangeCondition = conditions.stream()
                .noneMatch(condition -> condition.goalMetricType().hasPercentage());

        ChallengeAchievement achievement =
                new ChallengeAchievement(challenge, runningRecord, failedCondition.isEmpty());

        if (hasNoRangeCondition) {
            return new ChallengeAchievementRecord(achievement);
        }

        ChallengeCondition relevantCondition = failedCondition.orElseGet(() -> conditions.stream()
                .filter(condition -> condition.goalMetricType().hasPercentage())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ranged challenge condition found.")));

        PercentageValues percentageValues = new PercentageValues(
                relevantCondition.goalMetricType().getActualValue(runningRecord),
                0,
                relevantCondition.comparisonValue());

        return new ChallengeAchievementRecord(achievement, percentageValues);
    }
}
