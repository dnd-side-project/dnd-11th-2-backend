package com.dnd.runus.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BadgeType {
    STREAK("연속"),
    DISTANCE_METER("러닝거리"),
    PERSONAL_RECORD("개인기록"),
    DURATION_SECONDS("시간"),
    LEVEL("레벨"),
    ;
    private final String name;
}
