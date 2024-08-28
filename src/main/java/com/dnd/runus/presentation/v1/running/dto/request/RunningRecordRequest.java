package com.dnd.runus.presentation.v1.running.dto.request;

import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RunningRecordRequest(
        @NotNull
        LocalDateTime startAt,
        @NotNull
        LocalDateTime endAt,
        @NotBlank
        @Schema(description = "시작 위치", example = "서울시 강남구")
        String startLocation,
        @NotBlank
        @Schema(description = "종료 위치", example = "서울시 송파구")
        String endLocation,
        @NotNull
        RunningEmoji emoji,
        @Schema(description = "챌린지 ID, 챌린지를 하지 않은 경우 null이나 필드 없이 보내주세요", example = "1")
        Long challengeId,
        @Schema(description = "개인 목표 거리 (미터), 개인 목표가 아닌 경우, null이나 필드 없이 보내주세요", example = "5000")
        Integer goalDistance,
        @Schema(description = "개인 목표 시간 (초), 개인 목표가 아닌 경우, null이나 필드 없이 보내주세요", example = "1800")
        Integer goalTime,
        @NotNull
        @Schema(description = "목표 달성 모드, NORMAL: 목표 설정X, CHALLENGE: 챌린지, GOAL: 개인 목표")
        RunningAchievementMode achievementMode,
        @NotNull
        RunningRecordMetricsDto runningData
) {
}
