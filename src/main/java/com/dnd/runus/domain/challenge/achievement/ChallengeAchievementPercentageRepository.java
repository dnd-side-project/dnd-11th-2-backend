package com.dnd.runus.domain.challenge.achievement;

public interface ChallengeAchievementPercentageRepository {
    // TODO 챌린지 성취(challenge_achievement)에 따라 퍼센티이지 값들을 저장하는 함수
    PercentageValues save(ChallengeAchievementRecord challengeAchievementRecord);
}
