package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ChallengeDataRepositoryImplTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @DisplayName("어제 기록이 있는경우 챌린지 리스트 조회 : 챌린지 타입 중 어제의 기록과 관련된 타입이 있어야 한다.")
    @Test
    void getChallengesWithYesterdayRecords() {
        // given
        boolean hasYesterdayRecord = true;

        // when
        List<ChallengeData.Challenge> challenges = challengeRepository.findAllChallenges(hasYesterdayRecord);

        // then
        assertTrue(challenges.stream().anyMatch(ChallengeData.Challenge::isDefeatYesterdayChallenge));
    }

    @DisplayName("어제 기록이 없는 경우 챌린지 리스트 조회 : 챌린지 타입 중 어제의 기록과 관련된 타입이 없어야 한다.")
    @Test
    void getChallengesWithoutYesterdayRecords() {
        // given
        boolean hasYesterdayRecord = false;

        // when
        List<ChallengeData.Challenge> challenges = challengeRepository.findAllChallenges(hasYesterdayRecord);

        // then
        assertTrue(challenges.stream().noneMatch(ChallengeData.Challenge::isDefeatYesterdayChallenge));
    }

    @DisplayName("challengeId로 오늘의 챌린지 찾기")
    @Test
    void findChallengeById() {

        long challengeId = 1;

        ChallengeData challengeDataWithConditions =
                challengeRepository.findChallengeWithConditionsByChallengeId(challengeId);

        Assertions.assertThat(challengeDataWithConditions.challengeConditions().size())
                .isGreaterThan(0);
    }
}
