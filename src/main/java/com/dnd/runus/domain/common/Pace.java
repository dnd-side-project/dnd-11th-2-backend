package com.dnd.runus.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 러닝 페이스를 나타내는 클래스
 *
 * @param minute
 * @param second
 */
public record Pace(int minute, int second) {
    public static Pace ofSeconds(int seconds) {
        int minute = seconds / 60;
        int second = seconds % 60;
        return new Pace(minute, second);
    }

    @JsonCreator
    public static Pace from(String pace) {
        // e.g. 5'30"
        String[] paceArr = pace.split("'|''");
        return new Pace(Integer.parseInt(paceArr[0]), Integer.parseInt(paceArr[1]));
    }

    @JsonValue
    public String getPace() {
        return minute + "'" + second + "''";
    }

    public int toSeconds() {
        return minute * 60 + second;
    }
}
