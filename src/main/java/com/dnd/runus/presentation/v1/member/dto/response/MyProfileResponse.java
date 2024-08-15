package com.dnd.runus.presentation.v1.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyProfileResponse(
        @Schema(description = "현재 프로필 이미지")
        String profileImageUrl,
        @Schema(description = "지금까지 달린 거리")
        String currentKm,
        @Schema(description = "다음 레벨 이름")
        String nextLevelName,
        @Schema(description = "다음 레벨까지 남은 거리")
        String nextLevelKm
) {
}
