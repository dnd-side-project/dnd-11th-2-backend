package com.dnd.runus.presentation.v1.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoalResultDto(
        @Schema(description = "설정된 목표 제목", example = "2.5km 달성")
        String title,
        @Schema(description = "설정된 목표 결과 문구", example = "성공했어요!")
        String subTitle,
        @NotBlank
        @Schema(description = "챌린지 아이콘 이미지 URL")
        String iconUrl,
        @Schema(description = "설정된 목표 성공 여부")
        boolean isSuccess
) {
}
