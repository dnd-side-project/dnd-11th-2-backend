package com.dnd.runus.domain.goalAchievement;

import com.dnd.runus.domain.running.RunningRecord;

import java.util.List;
import java.util.Optional;

public interface GoalAchievementRepository {
    GoalAchievement save(GoalAchievement goalAchievement);

    Optional<GoalAchievement> findByRunningRecordId(Long runningRecordId);

    void deleteByRunningRecords(List<RunningRecord> runningRecords);
}
