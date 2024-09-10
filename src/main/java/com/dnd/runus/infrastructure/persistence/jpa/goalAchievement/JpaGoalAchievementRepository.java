package com.dnd.runus.infrastructure.persistence.jpa.goalAchievement;

import com.dnd.runus.infrastructure.persistence.jpa.goalAchievement.entity.GoalAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaGoalAchievementRepository extends JpaRepository<GoalAchievementEntity, Long> {

    Optional<GoalAchievementEntity> findByRunningRecordId(Long runningRecordId);

    void deleteAllByRunningRecordIdIn(List<Long> runningRecordIds);
}
