package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;

import java.time.Duration;
import java.util.List;

public record RunningRecordSummaryResponse(
        List<Info> records
) {
    public record Info(
            long runningRecordId,
            RunningEmoji emoji,
            String startLocation,
            String endLocation,
            int distanceMeter,
            Pace averagePace,
            Duration duration,
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
