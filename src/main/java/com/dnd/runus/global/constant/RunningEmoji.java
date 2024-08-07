package com.dnd.runus.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public enum RunningEmoji {
    VERY_BAD("very-bad"),
    BAD("bad"),
    NORMAL("normal"),
    GOOD("good"),
    VERY_GOOD("very-good"),
    ;
    private final String code;

    @JsonCreator
    public static RunningEmoji from(String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown RunningEmoji code: " + code));
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
