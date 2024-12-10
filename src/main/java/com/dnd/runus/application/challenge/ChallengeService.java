package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    private final RunningRecordRepository runningRecordRepository;

    public List<ChallengeWithCondition> getChallenges(long memberId) {
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);

        List<RunningRecord> runningRecords =
                runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight);

        List<ChallengeWithCondition> allChallengesWithConditions =
                new ArrayList<>(challengeRepository.findAllActiveChallengesWithConditions());

        // 어제 기록이 없으면
        if (runningRecords.isEmpty()) {
            allChallengesWithConditions = allChallengesWithConditions.stream()
                    .filter(challengeWithCondition ->
                            !challengeWithCondition.challenge().isDefeatYesterdayChallenge())
                    .collect(Collectors.toList());
        }

        // 랜덤으로 2개 리턴
        Random randomWithSeed = new Random(todayMidnight.toEpochSecond());
        Collections.shuffle(allChallengesWithConditions, randomWithSeed);
        List<ChallengeWithCondition> randomChallengeWithConditions = allChallengesWithConditions.subList(0, 2);

        if (!runningRecords.isEmpty()) {
            for (ChallengeWithCondition challenge : randomChallengeWithConditions) {
                if (challenge.challenge().isDefeatYesterdayChallenge()) {
                    // 어제의 기록과 관련된 챌린지면, 챌린지 비교할 값(성공 유무를 위한 목표 값) 재등록
                    challenge
                            .conditions()
                            .forEach(condition -> condition.registerComparisonValue(
                                    condition.goalMetricType().getActualValue(runningRecords.getFirst())));
                }
            }
        }

        return randomChallengeWithConditions;
    }
}
