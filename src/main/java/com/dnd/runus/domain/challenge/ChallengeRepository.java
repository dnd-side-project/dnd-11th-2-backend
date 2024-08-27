package com.dnd.runus.domain.challenge;

import java.util.List;

public interface ChallengeRepository {
    List<Challenge> findAllChallenges();

    List<Challenge> findAllIsNotDefeatYesterday();

    ChallengeWithCondition findChallengeWithConditionsByChallengeId(long challengeId);
}
