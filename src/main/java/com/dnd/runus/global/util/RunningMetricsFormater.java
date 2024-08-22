package com.dnd.runus.global.util;

public class RunningMetricsFormater {

    // todo -> Level.formatExp(int exp) 가져왔어요. 공통으로 사용하면 될 것 같습니다.
    public static String meterToKm(int meter) {
        double km = meter / 1000.0;
        String formatted = String.format("%.2f", km);

        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0*$", "");
        }

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + "km";
    }

    public static String secondToKoreanHHMM(int second) {
        int hour = second / 3600;
        int minute = (second % 3600) * 60;
        StringBuilder sb = new StringBuilder();

        if (hour != 0) {
            sb.append(hour).append("시간 ");
        }
        if (minute != 0) {
            sb.append(hour).append("분");
        }

        return sb.toString().trim();
    }
}
