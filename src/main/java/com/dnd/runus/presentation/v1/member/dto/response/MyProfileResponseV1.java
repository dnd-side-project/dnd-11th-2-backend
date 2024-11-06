package com.dnd.runus.presentation.v1.member.dto.response;

import com.dnd.runus.domain.level.Level;
import io.swagger.v3.oas.annotations.media.Schema;

public record MyProfileResponseV1(
    @Schema(description = "현재 프로필 이미지")
    String profileImageUrl,
    @Schema(description = "현재 레벨 이름")
    String currentLevelName,
    @Schema(description = "지금까지 달린 거리")
    String currentKm,
    @Schema(description = "다음 레벨 이름")
    String nextLevelName,
    @Schema(description = "다음 레벨까지 남은 거리")
    String nextLevelKm
) {

    public static MyProfileResponseV1 from(MyProfileResponse myProfileResponse) {
        return new MyProfileResponseV1(
            myProfileResponse.profileImageUrl(),
            Level.formatLevelName(myProfileResponse.currentLevel()),
            Level.formatExp(myProfileResponse.currentExpMeter()),
            Level.formatLevelName(myProfileResponse.nextLevel()),
            Level.formatExp(myProfileResponse.nextLevelEndExpMeter()
                - myProfileResponse.currentExpMeter())
        );
    }
}
