package com.dnd.runus.domain.challenge.achievement.dto;


import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.global.constant.RunningResultComment;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengeAchievementDto(
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
    public static ChallengeAchievementDto from(ChallengeAchievement achievement, Challenge challenge) {
        return new ChallengeAchievementDto(
            challenge.imageUrl(),
            challenge.name(),
            RunningResultComment.getComment(achievement.record().successStatus()),
            achievement.record().successStatus()
        );
    }
}
