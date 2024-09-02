package com.dnd.runus.presentation.v1.running.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record RunningRecordMonthlyDatesResponse(
        @Schema(description = "러닝 기록이 있는 날짜 목록")
        List<LocalDate> days
) {
}
