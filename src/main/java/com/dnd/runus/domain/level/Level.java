package com.dnd.runus.domain.level;

public record Level(long levelId, int expRangeStart, int expRangeEnd, String imageUrl) {
    public static String formatExp(int exp) {
        return exp / 1_000 + "km";
    }

    public static String formatLevelName(long level) {
        return "Level " + level;
    }
}
