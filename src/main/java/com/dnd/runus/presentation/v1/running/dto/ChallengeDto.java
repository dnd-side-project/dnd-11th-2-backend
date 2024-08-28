package com.dnd.runus.presentation.v1.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengeDto(
        long challengeId,
        @Schema(description = "챌린지 이름")
        @NotNull
        String title,
        @Schema(
                description = "예상 소요 시간",
                example = "25분"
        )
        String expectedTime,
        @Schema(description = "챌린지 이미지 URL")
        @NotNull
        String icon
) {
}
