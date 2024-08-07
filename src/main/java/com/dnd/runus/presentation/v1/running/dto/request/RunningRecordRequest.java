package com.dnd.runus.presentation.v1.running.dto.request;

import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordDataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordRequest(
        @NotNull
        LocalDateTime startAt,
        @NotNull
        LocalDateTime endAt,
        @NotNull
        RunningEmoji emoji,
        @Schema(description = "챌린지 ID, 챌린지를 하지 않은 경우 null이나 필드 없이 보내주세요", example = "1")
        Long challengeId,
        @NotNull
        RunningRecordDataDto runningData
) {
}
