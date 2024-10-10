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
    private static final String EMPTY_PACE = "-’--”";

    public Pace {
        if (minute < 0 || second < 0 || second >= SECONDS_PER_MINUTE) {
            throw new IllegalArgumentException("분 또는 초는 0 이상 60 미만의 값이어야 합니다.");
        }
    }

    public static Pace ofSeconds(int seconds) {
        int minute = seconds / SECONDS_PER_MINUTE;
        int second = seconds % SECONDS_PER_MINUTE;
        return new Pace(minute, second);
    }

    @JsonCreator
    public static Pace from(String pace) {
        // e.g. 5'30" or 5’30”
        if (pace.equals(EMPTY_PACE)) {
            return new Pace(0, 0);
        }
        String[] paceArr = pace.split("[’']|”");
        return new Pace(Integer.parseInt(paceArr[0]), Integer.parseInt(paceArr[1]));
    }

    @JsonValue
    public String getJsonValue() {
        if (minute == 0 && second == 0) {
            return EMPTY_PACE;
        }
        return minute + "’" + String.format("%02d", second) + "”";
    }

    public int toSeconds() {
        return minute * SECONDS_PER_MINUTE + second;
    }
}
