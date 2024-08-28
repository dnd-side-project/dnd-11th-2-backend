package com.dnd.runus.presentation.v1.running.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RunningAchievementMode {
    NORMAL,
    GOAL,
    CHALLENGE,
    ;

    @JsonCreator
    public RunningAchievementMode fromString(String value) {
        return RunningAchievementMode.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }
}
