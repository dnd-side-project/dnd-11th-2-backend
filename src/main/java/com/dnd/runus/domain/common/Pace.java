package com.dnd.runus.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_MINUTE;

/**
 * 러닝 페이스를 나타내는 클래스
 *
 * @param minute
 * @param second
 */
public record Pace(int minute, int second) {
    public static Pace ofSeconds(int seconds) {
        int minute = seconds / SECONDS_PER_MINUTE;
        int second = seconds % SECONDS_PER_MINUTE;
        return new Pace(minute, second);
    }

    @JsonCreator
    public static Pace from(String pace) {
        // e.g. 5'30" or 5’30”
        String[] paceArr = pace.split("[’']|”");
        return new Pace(Integer.parseInt(paceArr[0]), Integer.parseInt(paceArr[1]));
    }

    @JsonValue
    public String getPace() {
        return minute + "’" + second + "”";
    }

    public int toSeconds() {
        return minute * SECONDS_PER_MINUTE + second;
    }
}
