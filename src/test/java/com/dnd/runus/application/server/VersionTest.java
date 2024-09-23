package com.dnd.runus.application.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    void parse() {
        Version version = Version.parse("1.2.3");
        assertEquals(1, version.major());
        assertEquals(2, version.minor());
        assertEquals(3, version.patch());
    }

    @Test
    void parseWithoutPatch() {
        Version version = Version.parse("1.2");
        assertEquals(1, version.major());
        assertEquals(2, version.minor());
        assertEquals(0, version.patch());
    }

    @ParameterizedTest
    @MethodSource("compareToProvider")
    void compareTo(Version version1, Version version2, int expected) {
        assertEquals(expected, version1.compareTo(version2));
    }

    @Test
    void isOlderThan() {
        Version version = new Version(1, 2, 3);

        assertTrue(version.isOlderThan(new Version(1, 2, 4)));
        assertTrue(version.isOlderThan(new Version(1, 3, 0)));
        assertTrue(version.isOlderThan(new Version(2, 0, 0)));

        assertFalse(version.isOlderThan(new Version(1, 2, 3)));
        assertFalse(version.isOlderThan(new Version(1, 2, 2)));
        assertFalse(version.isOlderThan(new Version(1, 1, 0)));
        assertFalse(version.isOlderThan(new Version(0, 0, 0)));
    }

    private static Object[] compareToProvider() {
        return new Object[] {
            new Object[] {new Version(1, 2, 3), new Version(1, 2, 3), 0},
            new Object[] {new Version(1, 2, 3), new Version(1, 2, 4), -1},
            new Object[] {new Version(1, 2, 3), new Version(1, 3, 0), -1},
            new Object[] {new Version(1, 2, 3), new Version(2, 0, 0), -1},
            new Object[] {new Version(1, 2, 3), new Version(1, 2, 2), 1},
            new Object[] {new Version(1, 2, 3), new Version(1, 1, 0), 1},
            new Object[] {new Version(1, 2, 3), new Version(0, 0, 0), 1},
        };
    }
}
