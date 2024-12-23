package com.dnd.runus.presentation.v2.running.dto.response;

import com.dnd.runus.application.running.dto.RunningResultDto;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.running.dto.ChallengeDto;
import com.dnd.runus.presentation.v1.running.dto.GoalResultDto;
import com.dnd.runus.presentation.v2.running.dto.RouteDtoV2;
import com.dnd.runus.presentation.v2.running.dto.RouteDtoV2.Point;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public record RunningRecordResultResponseV2(
    long runningRecordId,
    @Schema(description = "러닝 시작 시간")
    LocalDateTime startAt,
    @Schema(description = "러닝 종료 시간")
    LocalDateTime endAt,
    @NotNull
    @Schema(description = "감정 표현, very-good: 최고, good: 좋음, soso: 보통, bad: 나쁨, very-bad: 최악")
    RunningEmoji emotion,
    @NotNull
    @Schema(description = "달성 모드, normal: 일반(목표 설정 X), challenge: 챌린지, goal: 목표")
    com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode achievementMode,
    //todo 챌린지, 및 goal 관련해서 다시 구성
    @Schema(description = "챌린지 정보, achievementMode가 challenge인 경우에만 값이 존재합니다.")
    ChallengeDto challenge,
    @Schema(description = "목표 결과 정보, achievementMode가 goal인 경우에만 값이 존재합니다.")
    GoalResultDto goal,
    @NotNull
    RunningRecordMetrics runningData
) {
    @Schema(name = "RunningRecordMetrics for Result V2", description = "러닝 경로 정보")
    public record RunningRecordMetrics(
        @Schema(description = "평균 페이스", example = "5'30''")
        Pace averagePace,
        @Schema(description = "멈춘 시간을 제외한 실제로 달린 시간", example = "123:45:56", format = "HH:mm:ss")
        Duration runningTime,
        @Schema(description = "달린 거리(m)", example = "1000")
        int distanceMeter,
        @Schema(description = "소모 칼로리(kcal)", example = "100")
        double calorie,
        @Schema(description = "러닝 경로, 러닝 경로가 없는 경우(V2 이전 버전에 저장된 러닝 기록) null값을 리턴")
        List<RouteDtoV2> route
    ) {
    }

    public static RunningRecordResultResponseV2 from(RunningResultDto runningRecordResult) {
        RunningRecord runningRecord = runningRecordResult.runningRecord();
        return new RunningRecordResultResponseV2(
            runningRecord.runningId(),
            runningRecord.startAt().toLocalDateTime(),
            runningRecord.endAt().toLocalDateTime(),
            runningRecord.emoji(),
            runningRecordResult.runningAchievementMode(),
            runningRecordResult.runningAchievementMode() != com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.CHALLENGE ? null
                : buildChallengeDto(runningRecordResult.challengeAchievement()),
            runningRecordResult.runningAchievementMode() != com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.GOAL ? null
                : buildGoalResultDto(runningRecordResult.goalAchievement()),
            new RunningRecordMetrics(
                runningRecord.averagePace(),
                runningRecord.duration(),
                runningRecord.distanceMeter(),
                runningRecord.calorie(),
                convertRouteDtoListFrom(runningRecord.route())
            )
        );
    }

    private static ChallengeDto buildChallengeDto(ChallengeAchievement achievement) {
        if (achievement == null) {
            return null;
        }
        return new ChallengeDto(
            achievement.challenge().challengeId(),
            achievement.challenge().name(),
            achievement.description(),
            achievement.challenge().imageUrl(),
            achievement.isSuccess()
        );
    }

    private static GoalResultDto buildGoalResultDto(GoalAchievement achievement) {
        if (achievement == null) {
            return null;
        }
        return new GoalResultDto(
            achievement.getTitle(),
            achievement.getDescription(),
            achievement.getIconUrl(),
            achievement.isAchieved()
        );
    }


    private static List<RouteDtoV2> convertRouteDtoListFrom(
        List<CoordinatePoint> runningRecordRoute) {
        // route가 null, empty, 또는 경로데이터를 사용하지 않았을 버전의 데이터 값 인경우 null를 리턴
        if (runningRecordRoute == null || runningRecordRoute.isEmpty()) {
            return null;
        }
        if (runningRecordRoute.size() <= 2 && runningRecordRoute.getFirst().isNullIsland()){
            return null;
        }
        return IntStream.range(0, runningRecordRoute.size() / 2)
            .mapToObj(i -> new RouteDtoV2(
                Point.from(runningRecordRoute.get(i * 2)),
                Point.from(runningRecordRoute.get(i * 2 + 1))
            ))
            .toList();
    }
}
