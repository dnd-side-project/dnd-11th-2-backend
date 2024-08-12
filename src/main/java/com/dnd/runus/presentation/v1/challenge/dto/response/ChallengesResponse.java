package com.dnd.runus.presentation.v1.challenge.dto.response;


import com.dnd.runus.domain.challenge.Challenge;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengesResponse(
    @Schema(example = "1")
    @NotNull
    Long challengeId,
    @Schema(description = "챌린지 이름")
    @NotNull
    String name,
    @Schema(description = "예상 소요 시간(단위 초), 페이스 관련 챌린지이면 0리턴")
    Integer expectedTime,
    @Schema(description = "챌린지 이미지 URL")
    @NotNull
    String imageUrl
)
{
    public static ChallengesResponse from(Challenge challenge) {
        return new ChallengesResponse(
            challenge.challengeId(),
            challenge.name(),
            challenge.expectedTime(),
            challenge.imageUrl()
        );
    }
}
