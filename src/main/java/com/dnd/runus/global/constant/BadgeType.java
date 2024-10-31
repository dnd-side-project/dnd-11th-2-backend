package com.dnd.runus.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BadgeType {
    PERSONAL_RECORD("개인기록", 1),
    DISTANCE_METER("러닝거리", 2),
    STREAK("연속", 3),
    DURATION_SECONDS("시간", 4),
    LEVEL("레벨", 5),
    ;
    private final String name;
    private final int showPriority;
}
