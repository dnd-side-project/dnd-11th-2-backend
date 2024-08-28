package com.dnd.runus.application.running;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordAddResultResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordMonthlySummaryResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;

@Service
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;
    private final ChallengeAchievementPercentageRepository percentageValuesRepository;
    private final ZoneOffset defaultZoneOffset;

    public RunningRecordService(
            RunningRecordRepository runningRecordRepository,
            MemberRepository memberRepository,
            MemberLevelRepository memberLevelRepository,
            ChallengeRepository challengeRepository,
            ChallengeAchievementRepository challengeAchievementRepository,
            ChallengeAchievementPercentageRepository percentageValuesRepository,
            @Value("${app.default-zone-offset}") ZoneOffset defaultZoneOffset) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberRepository = memberRepository;
        this.memberLevelRepository = memberLevelRepository;
        this.challengeRepository = challengeRepository;
        this.challengeAchievementRepository = challengeAchievementRepository;
        this.percentageValuesRepository = percentageValuesRepository;
        this.defaultZoneOffset = defaultZoneOffset;
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getRunningRecordDates(long memberId, int year, int month) {
        OffsetDateTime from = LocalDate.of(year, month, 1).atStartOfDay().atOffset((ZoneOffset.of(SERVER_TIMEZONE)));
        OffsetDateTime to = from.plusMonths(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);

        return records.stream()
                .map(RunningRecord::startAt)
                .map(OffsetDateTime::toLocalDate)
                .distinct()
                .sorted()
                .toList();
    }

    public RunningRecordSummaryResponse getRunningRecordSummaries(long memberId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(ZoneOffset.of(SERVER_TIMEZONE));
        OffsetDateTime to = from.plusDays(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);
        return RunningRecordSummaryResponse.from(records);
    }

    @Transactional
    public RunningRecordAddResultResponse addRunningRecord(long memberId, RunningRecordRequest request) {
        if (request.startAt().isAfter(request.endAt())) {
            throw new BusinessException(ErrorType.START_AFTER_END, request.startAt() + ", " + request.endAt());
        }
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        RunningRecord record = runningRecordRepository.save(RunningRecord.builder()
                .member(member)
                .startAt(request.startAt().atOffset(defaultZoneOffset))
                .endAt(request.endAt().atOffset(defaultZoneOffset))
                .emoji(request.emotion())
                .startLocation(request.startLocation())
                .endLocation(request.endLocation())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(request.runningData().averagePace())
                .build());

        memberLevelRepository.updateMemberLevel(memberId, request.runningData().distanceMeter());

        switch (request.achievementMode()) {
            case CHALLENGE -> {
                ChallengeAchievement challengeAchievement =
                        handleChallengeMode(request.challengeId(), memberId, record);
                return RunningRecordAddResultResponse.of(record, challengeAchievement);
            }
            case GOAL -> {
                // TODO: 개인 목표 달성 로직 추가
            }
        }
        return RunningRecordAddResultResponse.from(record);
    }

    @Transactional(readOnly = true)
    public RunningRecordMonthlySummaryResponse getMonthlyRunningSummery(long memberId) {

        OffsetDateTime startDateOfMonth =
                OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE)).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime startDateOfNextMonth = startDateOfMonth.plusMonths(1);

        int monthValue = startDateOfMonth.getMonthValue();

        int monthlyTotalDistance = runningRecordRepository.findTotalDistanceMeterByMemberId(
                memberId, startDateOfMonth, startDateOfNextMonth);

        MemberLevel.Current currentMemberLevel = memberLevelRepository.findByMemberIdWithLevel(memberId);

        long nextLevel = currentMemberLevel.level().levelId() + 1;
        int remainingKmToNextLevel = currentMemberLevel.level().expRangeEnd() - currentMemberLevel.currentExp();

        return new RunningRecordMonthlySummaryResponse(
                monthValue,
                monthlyTotalDistance,
                Level.formatLevelName(nextLevel),
                Level.formatExp(remainingKmToNextLevel));
    }

    private ChallengeAchievement handleChallengeMode(Long challengeId, long memberId, RunningRecord runningRecord) {
        if (challengeId == null) {
            throw new NotFoundException("Challenge ID is required for CHALLENGE mode");
        }
        ChallengeWithCondition challengeWithCondition = challengeRepository
                .findChallengeWithConditionsByChallengeId(challengeId)
                .orElseThrow(() -> new NotFoundException(Challenge.class, challengeId));

        if (challengeWithCondition.challenge().isDefeatYesterdayChallenge()) {
            OffsetDateTime todayMidnight = LocalDate.now(ZoneId.of(SERVER_TIMEZONE))
                    .atStartOfDay(ZoneId.of(SERVER_TIMEZONE))
                    .toOffsetDateTime();
            OffsetDateTime yesterday = todayMidnight.minusDays(1);
            RunningRecord yesterdayRecord =
                    runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight).stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(RunningRecord.class, memberId));

            challengeWithCondition
                    .conditions()
                    .forEach(condition -> condition.registerComparisonValue(
                            condition.goalMetricType().getActualValue(yesterdayRecord)));
        }

        ChallengeAchievementRecord achievementRecord = challengeWithCondition.getAchievementRecord(runningRecord);
        ChallengeAchievement achievement =
                challengeAchievementRepository.save(achievementRecord.challengeAchievement());
        if (achievementRecord.percentageValues() != null) {
            percentageValuesRepository.save(achievementRecord);
        }
        return achievement;
    }
}
