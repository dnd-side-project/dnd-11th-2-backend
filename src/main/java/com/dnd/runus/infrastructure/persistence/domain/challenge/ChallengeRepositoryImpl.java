package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChallengeRepositoryImpl implements ChallengeRepository {

    @Override
    public List<Challenge> getChallenges(boolean hasYesterdayRecord) {
        return ChallengeData.getChallenges(hasYesterdayRecord).stream()
                .map(ChallengeData::toDomain)
                .toList();
    }

    @Override
    public Optional<Challenge> findById(Long id) {
        return ChallengeData.getChallengeById(id).map(ChallengeData::toDomain);
    }
}
