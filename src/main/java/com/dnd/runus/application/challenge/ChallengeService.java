package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeData;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    private final RunningRecordRepository runningRecordRepository;

    public List<ChallengesResponse> getChallenges(long memberId) {
        // 1. 어제 기록 확인
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);

        boolean hasYesterdayRecords =
                runningRecordRepository.hasByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight);

        return challengeRepository.findAllChallenges(hasYesterdayRecords).stream()
                .map(ChallengesResponse::from)
                .toList();
    }

    // 챌린지 성취 로직(러닝 서비스쪽으로 옮기면 될 것 같아요.
    public void saveTest(RunningRecord runningRecord, Member member) {
        // challenge 조건 까지 select
        ChallengeData challenge = challengeRepository.findChallengeWithConditionsByChallengeId(member.memberId());

        if (challenge.challengeInfo().isDefeatYesterdayChallenge()) {
            // 어제 기록 확인
            OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                    .atStartOfDay(SERVER_TIMEZONE_ID)
                    .toOffsetDateTime();
            OffsetDateTime yesterday = todayMidnight.minusDays(1);
            RunningRecord yesterdayRecord = runningRecordRepository
                    .findByMemberIdAndStartAtBetween(member.memberId(), yesterday, todayMidnight)
                    .get(0);

            challenge
                    .challengeConditions()
                    .forEach(condition -> condition.registerComparisonValue(
                            condition.goalType().getActualValue(yesterdayRecord)));
        }

        // 아래 리턴 값으로 챌린지 성취와와 퍼센테이지 저장
        getChallengeAchievement(runningRecord, challenge);
    }

    private ChallengeAchievementRecord getChallengeAchievement(
            RunningRecord runningRecord, ChallengeData challengeDataWithConditions) {
        boolean allSuccess = true;
        boolean allHasPercentage = true;
        ChallengeAchievementRecord.PercentageValues percentageValues = null;

        for (ChallengeCondition condition : challengeDataWithConditions.challengeConditions()) {
            boolean success = condition.isAchieved(condition.goalType().getActualValue(runningRecord));

            allSuccess &= success;
            if (!condition.hasPercentage()) allHasPercentage = false;

            if (allHasPercentage) {
                percentageValues = new ChallengeAchievementRecord.PercentageValues(
                        condition.goalType().getActualValue(runningRecord), 0, condition.requiredValue());
            } else {
                percentageValues = null;
            }
        }

        ChallengeAchievementRecord.ChallengeAchievement challengeAchievement =
                new ChallengeAchievementRecord.ChallengeAchievement(
                        challengeDataWithConditions.challengeInfo().challengeId(), runningRecord, allSuccess);

        return new ChallengeAchievementRecord(challengeAchievement, percentageValues);
    }
}
