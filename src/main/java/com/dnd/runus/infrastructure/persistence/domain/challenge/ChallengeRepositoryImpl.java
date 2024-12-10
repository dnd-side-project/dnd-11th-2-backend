package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepository {

    private final JooqChallengeRepository jooqChallengeRepository;

    @Override
    public List<ChallengeWithCondition> findAllActiveChallengesWithConditions() {
        return jooqChallengeRepository.findAllActiveChallengesWithConditions();
    }

    @Override
    public Optional<ChallengeWithCondition> findChallengeWithConditionsByChallengeId(long challengeId) {
        return Optional.ofNullable(jooqChallengeRepository.findChallengeWithConditionsBy(challengeId));
    }
}
