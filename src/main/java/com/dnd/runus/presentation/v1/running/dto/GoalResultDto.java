package com.dnd.runus.presentation.v1.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GoalResultDto(
        @Schema(description = "설정된 목표 제목", example = "2.5km 달성")
        String title,
        @Schema(description = "설정된 목표 결과 문구", example = "성공했어요!")
        String subTitle,
        @Schema(description = "설정된 목표 성공 여부")
        boolean isSuccess
) {
}
