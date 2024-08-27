package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepository {

    private final JooqChallengeRepository jooqChallengeRepository;

    @Override
    public List<ChallengeData.Challenge> findAllChallenges(boolean hasYesterdayRecord) {
        return jooqChallengeRepository.findAllBy(hasYesterdayRecord);
    }

    @Override
    public ChallengeData findChallengeWithConditionsByChallengeId(long challengeId) {
        return jooqChallengeRepository.findChallengeWithConditionsBy(challengeId);
    }
}
