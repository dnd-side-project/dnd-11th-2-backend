package com.dnd.runus.domain.challenge.achievement.dto.response;

import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievementRecord;
import com.dnd.runus.global.constant.RunningResultComment;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public record ChallengeAchievementResponse(
    @NotNull
    @Schema(description = "챌린지 이미지 url")
    String icon,
    @NotNull
    @Schema(description = "챌린지 이름")
    String title,
    @NotNull
    @Schema(
        description = "챌린지 성공, 실패 멘트",
        example = "정말 대단해요! 잘하셨어요"
    )
    String subtitle,
    @NotNull
    @Schema(description = "챌린지 성공, 실패 여부")
    Boolean isSuccess
) {
    public static ChallengeAchievementResponse from(
        ChallengeAchievementRecord challengeAchievementRecord, ChallengeData.Challenge challenge) {
        return new ChallengeAchievementResponse(
            challenge.imageUrl(),
            challenge.name(),
            RunningResultComment.getComment(challengeAchievementRecord.challengeAchievement().isSuccess()),
            challengeAchievementRecord.challengeAchievement().isSuccess()
        );
    }
}
