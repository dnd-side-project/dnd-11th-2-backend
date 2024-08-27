package com.dnd.runus.domain.challenge.achievement;

import java.util.Optional;

public interface ChallengeAchievementRepository {

    // TODO 챌린지 성취 값을 저장합니다.(러닝 완료 후, 러닝 타입이 챌린지이였을 경우 챌린지 성취 저장)
    ChallengeAchievement save(ChallengeAchievement challengeAchievement);

    // TODO 챌린지 성취 값을 조회합니다.(러닝 결과의 챌린지 결과 조회 시 사용)
    Optional<ChallengeAchievement> findByRunningId(Long runningId);
}
