package com.dnd.runus.domain.level;

public record Level(long levelId, int expRangeStart, int expRangeEnd, String imageUrl) {
    public static String formatExp(int exp) {
        double km = exp / 1000.0;
        String formatted = String.format("%.2f", km);

        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0*$", "");
        }

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + "km";
    }

    public static String formatLevelName(long level) {
        return "Level " + level;
    }
}
