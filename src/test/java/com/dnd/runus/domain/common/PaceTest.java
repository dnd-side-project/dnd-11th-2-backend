package com.dnd.runus.domain.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaceTest {
    private Pace pace;

    @BeforeEach
    void setUp() {
        pace = new Pace(5, 30);
    }

    @Test
    @DisplayName("초를 입력받아 Pace 객체를 생성한다.")
    void ofSeconds() {
        assertEquals(Pace.ofSeconds(330), pace);
    }

    @Test
    @DisplayName("올바른 형태의 문자열을 입력받아 Pace 객체를 생성한다.")
    void from() {
        assertEquals(Pace.from("5'30''"), pace);
        assertEquals(Pace.from("5’30”"), pace);
    }

    @Test
    @DisplayName("Pace 객체를 올바른 형태의 문자열로 변환한다.")
    void getPace() {
        assertEquals("5’30”", pace.getPace());
    }

    @Test
    @DisplayName("Pace 객체를 초(60 * 분 + 초)로 변환한다.")
    void toSeconds() {
        assertEquals(330, pace.toSeconds());
    }
}
