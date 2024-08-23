package com.dnd.runus.infrastructure.persistence.jpa.goalAchievement.entity;

import com.dnd.runus.domain.challenge.GoalType;
import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "goal_achievement")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class GoalAchievementEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = LAZY)
    private RunningRecordEntity runningRecord;

    @NotNull
    @Enumerated(STRING)
    private GoalType goalType;

    @NotNull
    private Integer achievementValue;

    @NotNull
    private Boolean isAchieved;

    public static GoalAchievementEntity from(GoalAchievement goalAchievement) {
        return GoalAchievementEntity.builder()
                .runningRecord(RunningRecordEntity.from(goalAchievement.runningRecord()))
                .goalType(goalAchievement.goalType())
                .achievementValue(goalAchievement.achievementValue())
                .isAchieved(goalAchievement.isAchieved())
                .build();
    }

    public GoalAchievement toDomain() {
        return new GoalAchievement(runningRecord.toDomain(), goalType, achievementValue, isAchieved);
    }
}
