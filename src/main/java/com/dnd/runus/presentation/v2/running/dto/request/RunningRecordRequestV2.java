package com.dnd.runus.presentation.v2.running.dto.request;


import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v2.running.dto.RouteDtoV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record RunningRecordRequestV2(
        @NotNull
        LocalDateTime startAt,
        @NotNull
        LocalDateTime endAt,
        @NotBlank
        @Schema(description = "시작 위치", example = "서울시 강남구")
        String startLocation,
        @NotBlank
        @Schema(description = "종료 위치", example = "서울시 송파구")
        String endLocation,
        @NotNull
        @Schema(description = "감정 표현, very-good: 최고, good: 좋음, soso: 보통, bad: 나쁨, very-bad: 최악")
        RunningEmoji emotion,
        @NotNull
        @Schema(description = "목표 달성 모드, normal: 목표 설정X, challenge: 챌린지, goal: 개인 목표")
        com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode achievementMode,
        @Schema(description = "챌린지 데이터, 챌린지를 하지 않은 경우 null이나 필드 없이 보내주세요")
        ChallengeAchievedDto challengeValues,
        @Schema(description = "목표 데이터, 목표를 설정하지 않은 경우 null이나 필드 없이 보내주세요. "
            + "goalDistance(거리) 또는 goalTime(시간)값 둘 중 하나는 null이어야 합니다.")
        GoalAchievedDto goalValues,
        @NotNull
        RunningRecordMetrics runningData
) {
    public RunningRecordRequestV2 {
        //request valid check
        //시작, 종료 시간 유효값 확인
        if (startAt.isAfter(endAt)) {
            throw new BusinessException(ErrorType.START_AFTER_END, startAt + " ~ " + endAt);
        }

        // 러닝 모드 유요성 확인
        if (achievementMode == com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE && challengeValues == null) {
            throw new BusinessException(ErrorType.CHALLENGE_VALUES_REQUIRED_IN_CHALLENGE_MODE);
        }
        
        if (achievementMode == com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.GOAL) {
            if(goalValues == null) {
                throw new BusinessException(ErrorType.GOAL_VALUES_REQUIRED_IN_GOAL_MODE);
            }
            if ((goalValues.goalDistance() == null && goalValues.goalTime() == null)
                || (goalValues.goalDistance() != null && goalValues.goalTime() != null)) {
                throw new BusinessException(ErrorType.GOAL_TIME_AND_DISTANCE_BOTH_EXIST);
            }
        }

        //러닝 경로 유요성 확인
        if(runningData.route() == null || runningData.route().size() < 2) {
            throw new BusinessException(ErrorType.ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES);
        }
    }

    public record ChallengeAchievedDto(
        @Schema(description = "챌린지 ID", example = "1")
        long challengeId,
        boolean isSuccess
    ) {
    }

    public record GoalAchievedDto(
        @Schema(description = "개인 목표 거리 (미터), 거리 목표가 아닌 경우, null이나 필드 없이 보내주세요", example = "5000")
        Integer goalDistance,
        @Schema(description = "개인 목표 시간 (초), 시간 목표가 아닌 경우, null이나 필드 없이 보내주세요", example = "1800")
        Integer goalTime,
        boolean isSuccess
    ) {
    }

    @Schema(name = "RunningRecordMetrics for Add V2")
    public record RunningRecordMetrics(
        @NotNull
        @Schema(description = "멈춘 시간을 제외한 실제로 달린 시간", example = "123:45:56", format = "HH:mm:ss")
        Duration runningTime,
        @Schema(description = "달린 거리(m)", example = "1000")
        @NotNull
        int distanceMeter,
        @Schema(description = "소모 칼로리(kcal)", example = "100")
        @NotNull
        double calorie,
        @NotNull
        @Schema(description = "러닝 경로, 최소, 경로는 최소 2개의 좌표를 가져야합니다.")
        List<RouteDtoV2> route
    ) {
    }
}
