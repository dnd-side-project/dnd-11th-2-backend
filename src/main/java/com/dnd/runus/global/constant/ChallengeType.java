package com.dnd.runus.global.constant;

/**
 * ChallengeType은 아래와 같은 타입을 나타낸다.
 * <p>{@code DEFEAT_YESTERDAY} : 어제의 기록을 이기는 타입
 * <p>ex) 어제보다 5분동안 더 달리기, 어제보다 1km더 달리기 등
 * <p>{@code TODAY} : 오늘의 챌린지
 * <p>ex) 오늘 3km달리기, 오늘 30분 달리기 등
 * <p>{@code DISTANCE_IN_TIME} : 지정 시간안에 일정 거리를 달리는 챌린지
 * <p>ex) 1km 6분 이내로 달리기, 5km 30분 이내로 달리기
 * <p>해당 챌린지는 페이스 + 거리로 확인한다. 예를 들어 1km 6분 이내로 달리기
 * 라면 평균 페이스 600이하 1km이상 달릴 시 성공, 5km 30분 이내로 달리기라면
 * 평균 페이스 600이하 5km이상 달릴 시 성공
 */
public enum ChallengeType {
    DEFEAT_YESTERDAY,
    TODAY,
    DISTANCE_IN_TIME,
}
