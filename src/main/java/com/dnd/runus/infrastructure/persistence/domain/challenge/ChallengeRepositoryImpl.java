package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepository {

    private final JooqChallengeRepository jooqChallengeRepository;
    private final JpaChallengeRepository jpaChallengeRepository;

    @Override
    public List<ChallengeWithCondition> findAllActiveChallengesWithConditions() {
        return jooqChallengeRepository.findAllActiveChallengesWithConditions();
    }

    @Override
    public Optional<ChallengeWithCondition> findChallengeWithConditionsByChallengeId(long challengeId) {
        return Optional.ofNullable(jooqChallengeRepository.findChallengeWithConditionsBy(challengeId));
    }

    @Override
    public Optional<Challenge> findById(long challengeId) {
        return jpaChallengeRepository.findById(challengeId).map(ChallengeEntity::toDomain);
    }
}
