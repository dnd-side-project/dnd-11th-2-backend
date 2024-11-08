package com.dnd.runus.presentation.v2.running.dto.response;


import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.DecimalFormat;
import lombok.Builder;


public record RunningRecordMonthlySummaryResponseV2(
    @Schema(description = "이번 달", example = "8월")
    String month,
    @Schema(description = "이번 달에 달린 키로 수", example = "2.55km")
    String monthlyKm,
    @Schema(description = "서브 메세지", example = "대전까지 00km 남았어요!")
    String message,
    @Schema(description = "퍼센테이지 시작 위치 이름", example = "인천")
    String startName,
    @Schema(description = "퍼센테이지 종료 위치 이름", example = "대전")
    String endName,
    @Schema(description = "퍼센테이지값", example = "0.728")
    double percentage
) {

    private static final DecimalFormat KILO_METER_FORMATTER = new DecimalFormat("#,###.##km");

    public RunningRecordMonthlySummaryResponseV2(
        int monthValue,
        int monthlyTotalMeter,
        PercentageBoxWithMessage percentageBoxWithMessage) {
        this(
            monthValue + "월",
            KILO_METER_FORMATTER.format(monthlyTotalMeter / METERS_IN_A_KILOMETER),
            percentageBoxWithMessage.message,
            percentageBoxWithMessage.percentageBox.startName,
            percentageBoxWithMessage.percentageBox.endName,
            percentageBoxWithMessage.percentageBox.percentage
        );
    }

    @Schema(name = "RunningRecordMonthlySummaryResponseV2 PercentageBox",
        description = "이번달 러닝 서머리 퍼센테이지 관련 값")
    @Builder
    public record PercentageBox(
        String startName,
        String endName,
        double percentage
    ) {
    }

    @Builder
    public record PercentageBoxWithMessage(
        String message,
        RunningRecordMonthlySummaryResponseV2.PercentageBox percentageBox
    ) {
    }

}
