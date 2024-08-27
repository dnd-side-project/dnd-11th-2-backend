package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.ChallengeData.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepository {

    private final JooqChallengeRepository jooqChallengeRepository;
    private final JpaChallengeRepository jpaChallengeRepository;

    @Override
    public List<ChallengeData.Challenge> findAllChallenges() {
        return jpaChallengeRepository.findAll().stream()
                .map(ChallengeEntity::toDomain)
                .toList();
    }

    @Override
    public List<Challenge> findAllIsNotDefeatYesterday() {
        return jooqChallengeRepository.findAllIsNotDefeatYesterday();
    }

    @Override
    public ChallengeData findChallengeWithConditionsByChallengeId(long challengeId) {
        return jooqChallengeRepository.findChallengeWithConditionsBy(challengeId);
    }
}
