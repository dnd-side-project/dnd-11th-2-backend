package com.dnd.runus.domain.level;

import java.text.DecimalFormat;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

public record Level(long levelId, int expRangeStart, int expRangeEnd, String imageUrl) {
    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("0.##km");

    public static String formatExp(int exp) {
        return KILO_METER_FORMATTER.format(exp / METERS_IN_A_KILOMETER);
    }

    public static String formatLevelName(long level) {
        return "Level " + level;
    }
}
