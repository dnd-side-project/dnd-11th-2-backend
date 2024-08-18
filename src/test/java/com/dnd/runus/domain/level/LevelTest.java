package com.dnd.runus.domain.level;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LevelTest {
    @DisplayName("Level 클래스의 formatExp 메서드는 경험치를 km 단위로 소수점 두 자리까지 0을 생략해서 표시한다.")
    @Test
    void formatExp() {
        assertEquals("0km", Level.formatExp(0));
        assertEquals("0.5km", Level.formatExp(500));
        assertEquals("0.56km", Level.formatExp(560));
        assertEquals("1km", Level.formatExp(1000));
        assertEquals("1.01km", Level.formatExp(1010));
        assertEquals("1.1km", Level.formatExp(1100));
        assertEquals("1.23km", Level.formatExp(1230));
        assertEquals("1.23km", Level.formatExp(1234));
    }

    @Test
    void formatLevelName() {
        assertEquals("Level 2", Level.formatLevelName(2));
    }
}
