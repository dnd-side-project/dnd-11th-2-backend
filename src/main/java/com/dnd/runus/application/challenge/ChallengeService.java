package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
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
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);

        // 어제 기록이 없으면
        if (!runningRecordRepository.hasByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight)) {
            return challengeRepository.findAllIsNotDefeatYesterday().stream()
                    .map(ChallengesResponse::from)
                    .toList();
        }

        return challengeRepository.findAllChallenges().stream()
                .map(ChallengesResponse::from)
                .toList();
    }

    // TODO 러닝 저장 후, 사용자 별 챌린지 성취 기록 저장 시 아래 saveTest(), getChallengeAchievement() 를 참고 하세요.
    //  saveTest(), getChallengeAchievement()의 주석을 참고해주세요.
    public void saveTest(RunningRecord runningRecord, Member member) {
        // 1.challenge의 조건 까지 select
        ChallengeWithCondition challenge =
                challengeRepository.findChallengeWithConditionsByChallengeId(member.memberId());

        // 2. 어제의 기록을 이기는 챌린지면
        if (challenge.challengeInfo().isDefeatYesterdayChallenge()) {
            // 3. 어제 기록 확인
            OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                    .atStartOfDay(SERVER_TIMEZONE_ID)
                    .toOffsetDateTime();
            OffsetDateTime yesterday = todayMidnight.minusDays(1);
            RunningRecord yesterdayRecord = runningRecordRepository
                    .findByMemberIdAndStartAtBetween(member.memberId(), yesterday, todayMidnight)
                    .get(0);

            // 4. 어제 기록과 챌린지 목표값을 계산해서 해당 챌린지에 달성해야 하는 비교값(comparisonValue) 등록
            challenge
                    .challengeConditions()
                    .forEach(condition -> condition.registerComparisonValue(
                            condition.goalType().getActualValue(yesterdayRecord)));
        }

        // 5. runningRecord와 ChallengeWithCondition으로 사용자의 챌린지 성취 기록 반환
        getChallengeAchievement(runningRecord, challenge);

        // 6. 5에서 반환한 값으로 챌린지 성취 기록 저장
        // ChallengeAchievementRepository의
        // ChallengeAchievement save(ChallengeAchievement challengeAchievement); 함수로 저장

        // 7. ChallengeAchievementRecord의 percentageValues가 null이 아닐 경우
        // 7-1. 퍼센티이지 값 저장
        //      ChallengeAchievementPercentageRepository의  PercentageValues save(..)로 퍼센티이 값을 저장합니다.
    }

    // TODO runningRecord와 ChallengeWithCondition으로 사용자의 챌린지 성취 기록 반환
    private ChallengeAchievementRecord getChallengeAchievement(
            RunningRecord runningRecord, ChallengeWithCondition challengeDataWithConditions) {
        boolean allSuccess = true; // 전체 조건 성공 시 성공 여부(true)
        boolean allHasPercentage = true; // 전체 조건에서 하나라도 퍼센테이지가 존재하지 않는 조건이 있으면 false
        PercentageValues percentageValues = null; // 전체 조건에서 하나라도 퍼센테이지가 존재하지 않는 조건이 있으면 null로

        // 챌린지의 조건에 따라 해당 러닝에서의 챌린지 완수(성취) 기록을 계산합니다.
        for (ChallengeCondition condition : challengeDataWithConditions.challengeConditions()) {
            boolean success = condition.isAchieved(condition.goalType().getActualValue(runningRecord));

            allSuccess &= success;
            if (!condition.hasPercentage()) allHasPercentage = false;

            if (allHasPercentage) {
                percentageValues = new PercentageValues(
                        condition.goalType().getActualValue(runningRecord), 0, condition.requiredValue());
            } else {
                percentageValues = null;
            }
        }

        ChallengeAchievement challengeAchievement = new ChallengeAchievement(
                challengeDataWithConditions.challengeInfo().challengeId(), runningRecord, allSuccess);

        return new ChallengeAchievementRecord(challengeAchievement, percentageValues);
    }
}
