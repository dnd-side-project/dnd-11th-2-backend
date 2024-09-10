package com.dnd.runus.domain.level;

import java.text.DecimalFormat;

public record Level(long levelId, int expRangeStart, int expRangeEnd, String imageUrl) {
    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("0.##km");

    public static String formatExp(int exp) {
        return KILO_METER_FORMATTER.format(exp / 1000.0);
    }

    public static String formatLevelName(long level) {
        return "Level " + level;
    }
}
