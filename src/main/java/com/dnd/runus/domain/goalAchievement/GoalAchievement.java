package com.dnd.runus.domain.goalAchievement;

import com.dnd.runus.domain.challenge.GoalType;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.util.RunningMetricsFormater;

import static com.dnd.runus.domain.challenge.GoalType.DISTANCE;

public record GoalAchievement(
        Member member, RunningRecord runningRecord, GoalType goalType, Integer achievementValue, Boolean isAchieved) {
    public GoalAchievement(Member member, RunningRecord runningRecord, GoalType goalType, Integer achievementValue) {
        this(
                member,
                runningRecord,
                goalType,
                achievementValue,
                goalType.getActualValue(runningRecord) >= achievementValue);
    }

    public String getTitle() {
        if (goalType == DISTANCE) {
            String km = RunningMetricsFormater.meterToKm(achievementValue).toUpperCase();
            return km + " 달성";
        }

        return RunningMetricsFormater.secondToKoreanHHMM(achievementValue) + " 달성";
    }
}
