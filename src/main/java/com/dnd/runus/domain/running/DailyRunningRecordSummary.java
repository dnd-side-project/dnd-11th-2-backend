package com.dnd.runus.domain.running;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyRunningRecordSummary(@NotNull LocalDate date, int sumValue) {
    public DailyRunningRecordSummary(LocalDate date, Integer sumValue) {
        this(date, sumValue == null ? 0 : sumValue);
    }
}
