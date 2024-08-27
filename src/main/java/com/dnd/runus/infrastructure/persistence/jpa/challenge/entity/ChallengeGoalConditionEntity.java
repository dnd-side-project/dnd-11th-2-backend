package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.ComparisonType;
import com.dnd.runus.domain.challenge.GoalType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Entity(name = "challenge_goal_condition")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ChallengeGoalConditionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private ChallengeEntity challenge;

    @NotNull
    @Enumerated(STRING)
    private GoalType goalType;

    @NotNull
    private Integer goalValue;

    @NotNull
    @Enumerated(STRING)
    private ComparisonType comparisonType;
}
