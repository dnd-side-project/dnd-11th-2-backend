package com.dnd.runus.domain.challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository {
    List<Challenge> getChallenges(boolean hasPreRecord);

    Optional<Challenge> findById(Long id);
}
