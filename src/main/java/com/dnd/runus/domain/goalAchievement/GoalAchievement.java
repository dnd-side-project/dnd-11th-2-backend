package com.dnd.runus.domain.goalAchievement;

import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.running.RunningRecord;

import java.text.DecimalFormat;

import static com.dnd.runus.domain.challenge.GoalMetricType.DISTANCE;
import static com.dnd.runus.global.constant.RunningResultComment.FAILURE;
import static com.dnd.runus.global.constant.RunningResultComment.SUCCESS;

public record GoalAchievement(
        RunningRecord runningRecord, GoalMetricType goalMetricType, int achievementValue, boolean isAchieved) {

    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("0.##km");
    private static final String SUCCESS_ICON_URL = "https://d27big3ufowabi.cloudfront.net/goal/goal-success.png";
    private static final String FAILURE_ICON_URL = "https://d27big3ufowabi.cloudfront.net/goal/goal-failure.png";

    public GoalAchievement(RunningRecord runningRecord, GoalMetricType goalMetricType, int achievementValue) {
        this(
                runningRecord,
                goalMetricType,
                achievementValue,
                goalMetricType.getActualValue(runningRecord) >= achievementValue);
    }

    public String getTitle() {
        if (goalMetricType == DISTANCE) {
            return KILO_METER_FORMATTER.format(achievementValue / 1000.0) + " 달성";
        }

        return formatSecondToKoreanHHMM(achievementValue) + " 달성";
    }

    public String getDescription() {
        return isAchieved ? SUCCESS.getComment() : FAILURE.getComment();
    }

    public String getIconUrl() {
        return isAchieved ? SUCCESS_ICON_URL : FAILURE_ICON_URL;
    }

    private String formatSecondToKoreanHHMM(int second) {
        int hour = second / 3600;
        int minute = (second % 3600) / 60;
        StringBuilder sb = new StringBuilder();

        if (hour != 0) {
            sb.append(hour).append("시간 ");
        }
        if (minute != 0) {
            sb.append(minute).append("분");
        }

        return sb.toString().trim();
    }
}
