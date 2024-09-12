package com.dnd.runus.domain.running;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;

public record RunningRecordWeeklySummary(@NotNull LocalDate date, Integer sumDistanceMeter, Duration sumDuration) {

    public RunningRecordWeeklySummary(LocalDate date, Integer sumDistanceMeter) {
        this(date, sumDistanceMeter, null);
    }

    public RunningRecordWeeklySummary(LocalDate date, Duration sumDuration) {
        this(date, null, sumDuration);
    }
}
