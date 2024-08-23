package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.dto.ChallengeAchievementDto;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;

    private final RunningRecordRepository runningRecordRepository;

    public List<ChallengesResponse> getChallenges(long memberId) {
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);

        boolean hasYesterdayRecords =
                runningRecordRepository.hasByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight);

        return challengeRepository.getChallenges(hasYesterdayRecords).stream()
                .map(ChallengesResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChallengeAchievementDto save(RunningRecord runningRecord, long challengeId) {
        Challenge challenge = challengeRepository
                .findById(challengeId)
                .orElseThrow(() -> new NotFoundException(Challenge.class, challengeId));

        if (challenge.isDefeatYesterdayChallenge()) {
            OffsetDateTime midnight = runningRecord
                    .startAt()
                    .toLocalDate()
                    .atStartOfDay(runningRecord.startAt().getOffset())
                    .toOffsetDateTime();
            OffsetDateTime yesterdayMidnight = midnight.minusDays(1);

            RunningRecord yesterdayRecord = runningRecordRepository
                    .findByMemberIdAndStartAtBetween(runningRecord.member().memberId(), yesterdayMidnight, midnight)
                    .get(0);

            challenge
                    .conditions()
                    .forEach(condition -> condition.registerComparisonValue(
                            condition.goalType().getActualValue(yesterdayRecord)));
        }

        ChallengeAchievementRecord achievementRecord = challenge.getAchievementRecord(runningRecord);

        ChallengeAchievement savedAchievement = challengeAchievementRepository.save(
                new ChallengeAchievement(runningRecord, challengeId, achievementRecord));

        return ChallengeAchievementDto.from(savedAchievement, challenge);
    }

    @Transactional(readOnly = true)
    public ChallengeAchievementDto findChallengeAchievementBy(long runningId) {

        ChallengeAchievement challengeAchievement =
                challengeAchievementRepository.findByRunningRecordId(runningId).orElse(null);
        if (challengeAchievement == null) {
            return null;
        }

        Challenge challenge = challengeRepository
                .findById(challengeAchievement.challengeId())
                .orElseThrow(() -> new NotFoundException(Challenge.class, challengeAchievement.challengeId()));

        return ChallengeAchievementDto.from(challengeAchievement, challenge);
    }
}
