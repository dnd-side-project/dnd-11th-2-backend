package com.dnd.runus.domain.challenge;

import java.util.List;

public interface ChallengeRepository {
    List<ChallengeData.Challenge> findAllChallenges(boolean hasYesterdayRecord);

    ChallengeData findChallengeWithConditionsByChallengeId(long challengeId);
}
