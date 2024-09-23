package com.dnd.runus.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_MINUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaceTest {
    @ParameterizedTest
    @DisplayName("초를 입력받아 Pace 객체를 생성한다.")
    @ValueSource(ints = {330, 300})
    void ofSeconds(int seconds) {
        int minute = seconds / SECONDS_PER_MINUTE;
        int second = seconds % SECONDS_PER_MINUTE;
        Pace pace = new Pace(minute, second);
        assertEquals(Pace.ofSeconds(seconds), pace);
    }

    @ParameterizedTest
    @DisplayName("올바른 형태의 문자열을 입력받아 Pace 객체를 생성한다.")
    @ValueSource(strings = {"5'30''", "5’30”"})
    void from(String paceString) {
        Pace pace = new Pace(5, 30);
        assertEquals(Pace.from(paceString), pace);
    }

    @ParameterizedTest
    @DisplayName("Pace 객체를 올바른 형태의 문자열로 변환한다.")
    @MethodSource("providePace")
    void getJsonValue(int minute, int second, String expected) {
        Pace pace = new Pace(minute, second);
        assertEquals(expected, pace.getJsonValue());
    }

    @ParameterizedTest
    @DisplayName("Pace 객체를 초(60 * 분 + 초)로 변환한다.")
    @MethodSource("providePace")
    void toSeconds(int minute, int second) {
        int seconds = minute * SECONDS_PER_MINUTE + second;
        Pace pace = new Pace(minute, second);
        assertEquals(seconds, pace.toSeconds());
    }

    private static Stream<Arguments> providePace() {
        return Stream.of(
                Arguments.of(0, 0, "0’00”"),
                Arguments.of(0, 30, "0’30”"),
                Arguments.of(1, 1, "1’01”"),
                Arguments.of(1, 5, "1’05”"),
                Arguments.of(5, 0, "5’00”"),
                Arguments.of(5, 30, "5’30”"),
                Arguments.of(10, 0, "10’00”"),
                Arguments.of(10, 10, "10’10”"),
                Arguments.of(100, 10, "100’10”"));
    }

    @Test
    @DisplayName("음수 또는 60 이상의 값을 입력받으면 IllegalArgumentException을 던진다.")
    void constructor_exception() {
        assertThrows(IllegalArgumentException.class, () -> new Pace(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pace(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Pace(0, 60));
    }
}
