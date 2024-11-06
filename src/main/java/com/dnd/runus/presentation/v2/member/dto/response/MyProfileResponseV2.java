package com.dnd.runus.presentation.v2.member.dto.response;


import com.dnd.runus.domain.level.Level;
import com.dnd.runus.presentation.v1.member.dto.response.MyProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record MyProfileResponseV2 (
    @Schema(description = "현재 프로필 이미지")
    String profileImageUrl,
    @Schema(description = "현재 레벨 이름")
    String currentLevelName,
    @Schema(description = "지금까지 달린 거리")
    String currentKm,
    @Schema(description = "다음 레벨 이름")
    String nextLevelName,
    @Schema(description = "다음 레벨까지 남은 거리")
    String nextLevelKm,
    @Schema(description = "퍼센테이지값", example = "0.728")
    double percentage

) {

    public static MyProfileResponseV2 of(MyProfileResponse myProfileResponse, double percentage) {
        return new MyProfileResponseV2(
            myProfileResponse.profileImageUrl(),
            Level.formatLevelName(myProfileResponse.currentLevel()),
            Level.formatExp(myProfileResponse.currentExpMeter()),
            Level.formatLevelName(myProfileResponse.nextLevel()),
            Level.formatExp(myProfileResponse.nextLevelEndExpMeter()
                - myProfileResponse.currentExpMeter()),
            percentage
        );
    }

}
