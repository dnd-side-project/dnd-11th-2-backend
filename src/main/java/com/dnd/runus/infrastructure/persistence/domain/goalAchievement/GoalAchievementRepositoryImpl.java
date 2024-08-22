package com.dnd.runus.infrastructure.persistence.domain.goalAchievement;

import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.goalAchievement.JpaGoalAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.goalAchievement.entity.GoalAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GoalAchievementRepositoryImpl implements GoalAchievementRepository {

    private final JpaGoalAchievementRepository jpaGoalAchievementRepository;

    @Override
    public GoalAchievement save(GoalAchievement goalAchievement) {
        return jpaGoalAchievementRepository
                .save(GoalAchievementEntity.from(goalAchievement))
                .toDomain();
    }

    @Override
    public Optional<GoalAchievement> findByRunningRecordId(Long runningRecordId) {
        return jpaGoalAchievementRepository
                .findByRunningRecordId(runningRecordId)
                .map(GoalAchievementEntity::toDomain);
    }
}
