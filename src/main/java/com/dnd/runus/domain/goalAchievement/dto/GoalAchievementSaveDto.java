package com.dnd.runus.domain.goalAchievement.dto;

import com.dnd.runus.domain.challenge.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;


public record GoalAchievementSaveDto(
    @Schema(description = "목표 타입")
    GoalType goalType,
    //todo 프런트와 어떤 형식으로 받을 지 의논해야될 것 같아요.
    Integer goalValue
) {

}
