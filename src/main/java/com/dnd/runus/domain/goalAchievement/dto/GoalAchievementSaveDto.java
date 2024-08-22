package com.dnd.runus.domain.goalAchievement.dto;

import com.dnd.runus.domain.challenge.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;


public record GoalAchievementSaveDto(
    @Schema(description = "목표 타입")
    GoalType goalType,
    //프런트와 상의 예정
    //time, 거링에 따라 다르게 받을 지.....도 확인
    Integer goalValue
) {

}
