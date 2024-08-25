package com.dnd.runus.domain.goalAchievement.dto;


import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.global.constant.RunningResultComment;

public record GoalAchievementDto(
    String title,
    String comment,
    boolean isSuccess
) {
    public static GoalAchievementDto from(GoalAchievement achievement) {
        return new GoalAchievementDto(
            achievement.getTitle(),
            RunningResultComment.getComment(achievement.isAchieved()),
            achievement.isAchieved()
        );
    }
}
