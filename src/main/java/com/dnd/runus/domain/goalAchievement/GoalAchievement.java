package com.dnd.runus.domain.goalAchievement;

import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.running.RunningRecord;

import static com.dnd.runus.domain.challenge.GoalMetricType.DISTANCE;
import static com.dnd.runus.global.constant.RunningResultComment.FAILURE;
import static com.dnd.runus.global.constant.RunningResultComment.SUCCESS;

public record GoalAchievement(
        RunningRecord runningRecord, GoalMetricType goalMetricType, int achievementValue, boolean isAchieved) {
    public GoalAchievement(RunningRecord runningRecord, GoalMetricType goalMetricType, int achievementValue) {
        this(
                runningRecord,
                goalMetricType,
                achievementValue,
                goalMetricType.getActualValue(runningRecord) >= achievementValue);
    }

    public String getTitle() {
        if (goalMetricType == DISTANCE) {
            String km = formatMeterToKm(achievementValue);
            return km + " 달성";
        }

        return formatSecondToKoreanHHMM(achievementValue) + " 달성";
    }

    public String getDescription() {
        return isAchieved ? SUCCESS.getComment() : FAILURE.getComment();
    }

    private String formatMeterToKm(int meter) {
        String formatted = String.format("%.2f", (meter / 1000.0));

        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0*$", "");
        }

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + "KM";
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
