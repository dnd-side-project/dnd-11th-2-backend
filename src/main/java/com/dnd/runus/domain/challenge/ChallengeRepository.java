package com.dnd.runus.domain.challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository {
    List<Challenge> findAllChallenges();

    List<Challenge> findAllIsNotDefeatYesterday();

    Optional<ChallengeWithCondition> findChallengeWithConditionsByChallengeId(long challengeId);

    Challenge save(Challenge challenge);
}
