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

        // 어제 기록이 없으면 어제 기록과 관련된 챌린지 삭제
        if (runningRecords.isEmpty()) {
            allChallengesWithConditions.removeIf(
                    challengeWithCondition -> challengeWithCondition.challenge().isDefeatYesterdayChallenge());
        } else {
            // 어제의 기록과 관련된 챌린지면, 챌린지 비교할 값(성공 유무를 위한 목표 값) 재등록
            allChallengesWithConditions.stream()
                    .filter(challengeWithCondition ->
                            challengeWithCondition.challenge().isDefeatYesterdayChallenge())
                    .forEach(challengeWithCondition -> challengeWithCondition
                            .conditions()
                            .forEach(condition -> condition.registerComparisonValue(
                                    condition.goalMetricType().getActualValue(runningRecords.getFirst()))));
        }

        // 랜덤으로 2개 리턴
        Random randomWithSeed = new Random(todayMidnight.toEpochSecond());
        Collections.shuffle(allChallengesWithConditions, randomWithSeed);

        return allChallengesWithConditions.subList(0, 2);
    }
}
