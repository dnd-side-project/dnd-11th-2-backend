package com.dnd.runus.presentation.v1.running.dto.response;

import java.time.LocalDate;
import java.util.List;

public record RunningRecordMonthlyDatesResponse(
        List<LocalDate> days
) {
}