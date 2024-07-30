package com.dnd.runus.global.constant;

import java.time.ZoneId;

public final class TimeConstant {
    TimeConstant() {}

    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_EXAMPLE = "01:23:34";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_EXAMPLE = "2024-01-12";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_EXAMPLE = "2024-01-12 01:23:34";
    public static final String SERVER_TIMEZONE = "Asia/Seoul";
    public static final ZoneId SERVER_TIMEZONE_ID = ZoneId.of(SERVER_TIMEZONE);
}
