package com.dnd.runus.presentation.v1.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChallengeDto(
        @Schema(description = "챌린지 ID")
        long challengeId,
        @NotBlank
        @Schema(description = "챌린지 이름", example = "오늘 30분 동안 뛰기")
        String title,
        @NotBlank
        @Schema(description = "챌린지 결과 문구", example = "성공했어요!")
        String subTitle,
        @NotBlank
        @Schema(description = "챌린지 아이콘 이미지 URL")
        String iconUrl,
        @Schema(description = "챌린지 성공 여부")
        boolean isSuccess
) {
}
