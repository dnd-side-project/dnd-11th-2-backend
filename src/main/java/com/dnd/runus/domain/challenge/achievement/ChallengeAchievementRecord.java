package com.dnd.runus.domain.challenge.achievement;

/**
 * 사용자의 챌린지 성취 기록에 관한 DTO입니다.
 * @param successStatus 챌린지 성공 여부
 * @param hasPercentage 퍼센테이지 바 표시 유뮤(percentageValues이 null이면 false)
 * @param percentageValues 퍼센테이지 바 표시를 위한 값들 (hasPercentage가 false일 경우 null 값)
 */
public record ChallengeAchievementRecord(
        boolean successStatus, boolean hasPercentage, ChallengePercentageValues percentageValues) {
    public ChallengeAchievementRecord(boolean successStatus, ChallengePercentageValues percentageValues) {
        this(successStatus, percentageValues != null, percentageValues);
    }
}
