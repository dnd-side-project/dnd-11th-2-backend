package com.dnd.runus.domain.goalAchievement;

import java.util.Optional;

public interface GoalAchievementRepository {
    GoalAchievement save(GoalAchievement goalAchievement);

    Optional<GoalAchievement> findByRunningRecordId(Long runningRecordId);
}
