package com.dnd.runus.domain.challenge;

import java.util.List;

public interface ChallengeRepository {
    List<ChallengeData.Challenge> findAllChallenges();

    List<ChallengeData.Challenge> findAllIsNotDefeatYesterday();

    ChallengeData findChallengeWithConditionsByChallengeId(long challengeId);
}
