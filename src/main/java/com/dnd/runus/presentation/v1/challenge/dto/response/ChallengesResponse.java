package com.dnd.runus.presentation.v1.challenge.dto.response;


import com.dnd.runus.domain.challenge.Challenge;
import org.jetbrains.annotations.NotNull;

public record ChallengesResponse(
    @NotNull
    Long challengeId,
    @NotNull
    String name,
    Integer expectedTime,
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
