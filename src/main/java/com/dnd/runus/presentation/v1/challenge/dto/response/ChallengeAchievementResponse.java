package com.dnd.runus.presentation.v1.challenge.dto.response;


import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeAchievement;
import com.dnd.runus.global.constant.ChallengeResultComment;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengeAchievementResponse(
    @NotNull
    @Schema(description = "챌린지 이미지 url")
    String imageUrl,
    @NotNull
    @Schema(description = "챌린지 이름")
    String name,
    @NotNull
    @Schema(
        description = "챌린지 성공, 실패 멘트",
        example = "정말 대단해요! 잘하셨어요"
    )
    String comment,
    @NotNull
    @Schema(description = "챌린지 성공, 실패 여부")
    Boolean successStatus
) {
    public static ChallengeAchievementResponse from(ChallengeAchievement achievement, Challenge challenge) {
        return new ChallengeAchievementResponse(
            challenge.imageUrl(),
            challenge.name(),
            ChallengeResultComment.getComment(achievement.record().successStatus()),
            achievement.record().successStatus()
        );
    }
}
