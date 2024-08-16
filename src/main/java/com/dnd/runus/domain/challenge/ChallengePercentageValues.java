package com.dnd.runus.domain.challenge;

/**
 * 사용자의 챌린지 퍼센테이지를 표시하기 위해 필요한 DTO입니다.
 * @param myValue 사용자의 도전한 챌린지의 진행 상황 값
 * @param startValue 퍼센테이지 표시 시 시작점
 * @param endValue 퍼선테이지 표시 시 목표점(끝점)
 */
public record ChallengePercentageValues(Integer myValue, Integer startValue, Integer endValue, Integer percentage) {
    public ChallengePercentageValues(int myValue, int startValue, int endValue) {
        this(myValue, startValue, endValue, calPercentage(myValue, startValue, endValue));
    }

    private static int calPercentage(int myValue, int startValue, int endValue) {
        int percent = (int) Math.floor(((double) (myValue) / (endValue - startValue)) * 100);
        return Math.min(percent, 100);
    }
}
