package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.util.List;

public record RunningRecordSummaryResponse(
        List<Info> records
) {
    @Schema(name = "RunningRecordSummaryResponse Info", description = "달리기 기록 요약 정보")
    public record Info(
            long runningRecordId,
            RunningEmoji emoji,
            @Schema(description = "시작 위치", example = "서울시 강남구")
            String startLocation,
            @Schema(description = "종료 위치", example = "서울시 송파구")
            String endLocation,
            @Schema(description = "거리(m)", example = "1000")
            int distanceMeter,
            Pace averagePace,
            @Schema(description = "러닝 시간", example = "123:45:56", format = "HH:mm:ss")
            Duration duration,
            @Schema(description = "칼로리(kcal)", example = "100")
            double calorie
    ) {
    }

    public static RunningRecordSummaryResponse from(List<RunningRecord> records) {
        List<Info> infos = records.stream()
                .map(record -> new Info(
                        record.runningId(),
                        record.emoji(),
                        record.startLocation(),
                        record.endLocation(),
                        record.distanceMeter(),
                        record.averagePace(),
                        record.duration(),
                        record.calorie()
                ))
                .toList();
        return new RunningRecordSummaryResponse(infos);
    }
}
