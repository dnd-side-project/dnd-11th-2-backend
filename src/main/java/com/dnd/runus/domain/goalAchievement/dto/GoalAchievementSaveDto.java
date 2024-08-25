package com.dnd.runus.domain.goalAchievement.dto;

import com.dnd.runus.domain.challenge.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


public record GoalAchievementSaveDto(
    @Schema(description = "목표 타입")
    @NotNull
    GoalType goalType,
    @NotNull
    @Schema(description = "목표 값(시간:초단위, 거리:미터단위)")
    Integer goalValue
) {

}
