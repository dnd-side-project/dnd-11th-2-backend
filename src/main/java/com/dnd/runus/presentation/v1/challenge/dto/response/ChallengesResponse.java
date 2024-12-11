package com.dnd.runus.presentation.v1.challenge.dto.response;


import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.GoalMetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import java.util.List;
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
    String icon,
    @Schema(description = "챌린지 타입 : distance, time", example = "distance")
    GoalMetricType type,
    @Nullable
    @Schema(description = "챌린지 목표 거리(단위:km)", example = "5.0")
    Double goalDistance,
    @Nullable
    @Schema(description = "챌린지 목표 시간(단위:초)")
    Integer goalTime
) {
    public static ChallengesResponse from(ChallengeWithCondition challengeWithCondition) {
        Challenge challenge = challengeWithCondition.challenge();
        List<ChallengeCondition> conditions = challengeWithCondition.conditions();

        //현재는 복합 챌린지(페이스 + 거리 +..)가 없어 getFirst로 가져옵니다.
        //추후 페이스 관련 챌린지가 추가 되면 변경될 수 있습니다.
        ChallengeCondition condition = conditions.getFirst();
        return new ChallengesResponse(
            challenge.challengeId(),
            challenge.name(),
            challenge.formatExpectedTime(),
            challenge.imageUrl(),
            condition.goalMetricType(),
            condition.goalMetricType().equals(GoalMetricType.DISTANCE) ? calMeterToKm(condition.goalValue()) : null,
            condition.goalMetricType().equals(GoalMetricType.TIME) ? condition.goalValue() : null
        );
    }

    private static double calMeterToKm(int meter) {
        return meter / METERS_IN_A_KILOMETER;
    }
}
