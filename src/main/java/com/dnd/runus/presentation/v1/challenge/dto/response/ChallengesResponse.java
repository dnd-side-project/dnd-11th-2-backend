package com.dnd.runus.presentation.v1.challenge.dto.response;

import com.dnd.runus.domain.challenge.ChallengeData;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengesResponse(
    @Schema(example = "1")
    @NotNull
    Long challengeId,
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
    public static ChallengesResponse from(ChallengeData.Challenge challenge) {
        return new ChallengesResponse(
            challenge.challengeId(),
            challenge.name(),
            challenge.expectedTime(),
            challenge.imageUrl()
        );
    }
}
