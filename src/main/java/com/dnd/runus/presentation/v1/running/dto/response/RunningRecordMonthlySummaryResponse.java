package com.dnd.runus.presentation.v1.running.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;

//todo API 버저닝 관련 구조 정해지면 패키지 변경 예쩡
@Builder
public record RunningRecordMonthlySummaryResponse(
    int month,
    int monthlyTotalMeter
) {
}
