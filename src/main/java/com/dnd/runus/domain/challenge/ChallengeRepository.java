package com.dnd.runus.domain.challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository {
    List<ChallengeWithCondition> findAllActiveChallengesWithConditions();

    Optional<ChallengeWithCondition> findChallengeWithConditionsByChallengeId(long challengeId);
}
