package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeGoalType;
import com.dnd.runus.domain.challenge.ChallengeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.dnd.runus.domain.challenge.ChallengeGoalType.DISTANCE;
import static com.dnd.runus.domain.challenge.ChallengeGoalType.PACE;
import static com.dnd.runus.domain.challenge.ChallengeGoalType.TIME;
import static com.dnd.runus.domain.challenge.ChallengeType.DEFEAT_YESTERDAY;
import static com.dnd.runus.domain.challenge.ChallengeType.DISTANCE_IN_TIME;
import static com.dnd.runus.domain.challenge.ChallengeType.TODAY;

/**
 * ChallengeData 오늘의 챌린지 data입니다. (FIXME)
 * <p> 운영 후 새로운 챌린지를 추가할 일정이 생긴다면 해당 데이터를 테이블로 전환예정입니다.
 * <p>{@code expectedTime} : 예상 소모 시간(sec),
 * <p> 목표가 시간이면 해당 시간(분*60)으로, 거리면 거리(km)* 기본페이스(8분)*60 으로,
 * 거리+페이스면 거리면 거리(km)* 페이스(분)*60 으로, 페이스면 0으로 직접 입력합니다.
 * <p>{@code targetValues} : Map<GoalType, Integer> key: {@link ChallengeGoalType} 값, value:
 * GoalType과 관련된 목표 값
 * DISTANCE는 미터, TIME과 PACE는 초단위다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChallengeData {
    DEFEAT_YESTERDAY_DISTANCE_1KM(
            1L,
            "8분",
            "어제보다 1km더 뛰기",
            DEFEAT_YESTERDAY,
            Map.of(DISTANCE, 1000),
            "https://d27big3ufowabi.cloudfront.net/challenge_distance.png"),
    DEFEAT_YESTERDAY_TIME_5MINUTE(
            2L,
            "5분",
            "어제보다 5분 더 뛰기",
            DEFEAT_YESTERDAY,
            Map.of(TIME, 5 * 60),
            "https://d27big3ufowabi.cloudfront.net/challenge_time.png"),
    DEFEAT_YESTERDAY_PACE_10(
            3L,
            "0분",
            "어제보다 평균 페이스 10초 빠르게 뛰기",
            DEFEAT_YESTERDAY,
            Map.of(PACE, 10),
            "https://d27big3ufowabi.cloudfront.net/challenge_pace.png"),
    TODAY_DISTANCE_5KM(
            4L,
            "40분",
            "오늘 5km 뛰기",
            TODAY,
            Map.of(DISTANCE, 5 * 1000),
            "https://d27big3ufowabi.cloudfront.net/challenge_distance.png"),
    TODAY_TIME_30MINUTE(
            5L,
            "30분",
            "오늘 30분 동안 뛰기",
            TODAY,
            Map.of(TIME, 30 * 60),
            "https://d27big3ufowabi.cloudfront.net/challenge_time.png"),
    COMPLETE_1KM_IN_6MINUTE(
            6L,
            "6분",
            "1km 6분안에 뛰기",
            DISTANCE_IN_TIME,
            Map.of(DISTANCE, 1000, PACE, 6 * 30),
            "https://d27big3ufowabi.cloudfront.net/challenge_pace.png");

    private final long id;
    private final String expectedTime;
    private final String name;
    private final ChallengeType challengeType;
    private final Map<ChallengeGoalType, Integer> targetValues;
    private final String imageUrl;

    public static List<ChallengeData> getChallenges(boolean hasPreRecord) {
        return Arrays.stream(ChallengeData.values())
                .filter(data -> hasPreRecord || data.challengeType != DEFEAT_YESTERDAY)
                .toList();
    }

    public static Optional<ChallengeData> getChallengeById(long id) {
        return Arrays.stream(ChallengeData.values())
                .filter(data -> data.id == id)
                .findFirst();
    }

    public Challenge toDomain() {
        List<ChallengeCondition> conditions = targetValues.entrySet().stream()
                .map(entry -> new ChallengeCondition(
                        1, id, entry.getKey(), entry.getKey().getComparisonType(), entry.getValue()))
                .toList();
        return new Challenge(id, name, expectedTime, imageUrl, challengeType, conditions);
    }
}
