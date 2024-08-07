package com.dnd.runus.presentation.v1.running.dto.response;

import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordDataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordSavingResponse(
        long runningRecordId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        RunningEmoji emoji,
        String nickname,
        @Schema(description = "프로필 이미지 URL")
        String profileUrl,
        @NotNull
        RunningRecordDataDto runningData
) {
    public static RunningRecordSavingResponse from(RunningRecord runningRecord) {
        return new RunningRecordSavingResponse(
                runningRecord.runningId(),
                runningRecord.startAt().toLocalDateTime(),
                runningRecord.endAt().toLocalDateTime(),
                runningRecord.emoji(),
                runningRecord.member().nickname(),
                runningRecord.member().mainBadge() == null ? null : runningRecord.member().mainBadge().imageUrl(),
                new RunningRecordDataDto(
                        runningRecord.averagePace(),
                        runningRecord.location(),
                        runningRecord.duration(),
                        runningRecord.distanceMeter(),
                        runningRecord.calorie(),
                        runningRecord.route()
                ));
    }
}
