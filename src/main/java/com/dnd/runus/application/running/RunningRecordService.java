package com.dnd.runus.application.running;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordWeeklySummaryType;
import com.dnd.runus.presentation.v1.running.dto.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.time.*;
import java.util.List;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;
import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_HOUR;
import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;

@Service
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;
    private final ChallengeAchievementPercentageRepository percentageValuesRepository;
    private final GoalAchievementRepository goalAchievementRepository;
    private final ScaleRepository scaleRepository;
    private final ScaleAchievementRepository scaleAchievementRepository;

    private final ZoneOffset defaultZoneOffset;

    public RunningRecordService(
            RunningRecordRepository runningRecordRepository,
            MemberRepository memberRepository,
            MemberLevelRepository memberLevelRepository,
            ChallengeRepository challengeRepository,
            ChallengeAchievementRepository challengeAchievementRepository,
            ChallengeAchievementPercentageRepository percentageValuesRepository,
            GoalAchievementRepository goalAchievementRepository,
            ScaleRepository scaleRepository,
            ScaleAchievementRepository scaleAchievementRepository,
            @Value("${app.default-zone-offset}") ZoneOffset defaultZoneOffset) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberRepository = memberRepository;
        this.memberLevelRepository = memberLevelRepository;
        this.challengeRepository = challengeRepository;
        this.challengeAchievementRepository = challengeAchievementRepository;
        this.percentageValuesRepository = percentageValuesRepository;
        this.goalAchievementRepository = goalAchievementRepository;
        this.scaleRepository = scaleRepository;
        this.scaleAchievementRepository = scaleAchievementRepository;
        this.defaultZoneOffset = defaultZoneOffset;
    }

    @Transactional(readOnly = true)
    public RunningRecordQueryResponse getRunningRecord(long memberId, long runningRecordId) {
        RunningRecord record = runningRecordRepository
                .findById(runningRecordId)
                .filter(r -> r.member().memberId() == memberId)
                .orElseThrow(() -> new NotFoundException(RunningRecord.class, runningRecordId));
        return RunningRecordQueryResponse.from(record);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getRunningRecordDates(long memberId, int year, int month) {
        OffsetDateTime from = LocalDate.of(year, month, 1).atStartOfDay().atOffset(defaultZoneOffset);
        OffsetDateTime to = from.plusMonths(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);

        return records.stream()
                .map(RunningRecord::startAt)
                .map(OffsetDateTime::toLocalDate)
                .distinct()
                .sorted()
                .toList();
    }

    @Transactional(readOnly = true)
    public RunningRecordSummaryResponse getRunningRecordSummaries(long memberId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(defaultZoneOffset);
        OffsetDateTime to = from.plusDays(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);
        return RunningRecordSummaryResponse.from(records);
    }

    @Transactional(readOnly = true)
    public RunningRecordWeeklySummaryResponse getWeeklySummary(
            long memberId, RunningRecordWeeklySummaryType summaryType) {

        OffsetDateTime today = LocalDate.now().atStartOfDay().atOffset(defaultZoneOffset);
        OffsetDateTime startWeekDate = today.with(DayOfWeek.MONDAY);
        OffsetDateTime nextOfEndWeekDate = startWeekDate.plusWeeks(1);

        List<DailyRunningRecordSummary> weekSummaries;
        int avgValue;
        double conversionFactor;

        if (summaryType.equals(RunningRecordWeeklySummaryType.DISTANCE)) {
            weekSummaries = runningRecordRepository.findDailyDistancesMeterByDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            avgValue = runningRecordRepository.findAvgDistanceMeterByMemberIdAndDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            conversionFactor = METERS_IN_A_KILOMETER;
        } else {
            weekSummaries = runningRecordRepository.findDailyDurationsSecByDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            avgValue = runningRecordRepository.findAvgDurationSecByMemberIdAndDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            conversionFactor = SECONDS_PER_HOUR;
        }

        double[] weeklyValues = new double[7];
        for (DailyRunningRecordSummary summary : weekSummaries) {
            int dayOfWeek = summary.date().getDayOfWeek().getValue() - 1;
            weeklyValues[dayOfWeek] = summary.sumValue() / conversionFactor;
        }

        return new RunningRecordWeeklySummaryResponse(
                startWeekDate.toLocalDate(),
                startWeekDate.plusDays(6).toLocalDate(),
                weeklyValues,
                avgValue / conversionFactor);
    }

    @Transactional
    public RunningRecordAddResultResponse addRunningRecord(long memberId, RunningRecordRequest request) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        Coordinate emptyCoordinate = new Coordinate(0, 0, 0);
        List<Coordinate> route = List.of(emptyCoordinate, emptyCoordinate);

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
                .route(route)
                .build());

        memberLevelRepository.updateMemberLevel(memberId, request.runningData().distanceMeter());

        // 사용자가 달성할 수 있는 지구 한바퀴 코스 확인 후 저장
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(member.memberId());
        if (achievableScaleIds != null && !achievableScaleIds.isEmpty() && achievableScaleIds.get(0) != null) {
            saveScaleAchievements(achievableScaleIds, member);
        }

        switch (request.achievementMode()) {
            case CHALLENGE -> {
                ChallengeAchievement challengeAchievement =
                        handleChallengeMode(request.challengeId(), memberId, record);
                return RunningRecordAddResultResponse.of(record, challengeAchievement);
            }
            case GOAL -> {
                GoalAchievement goalAchievement = handleGoalMode(record, request.goalDistance(), request.goalTime());
                return RunningRecordAddResultResponse.of(record, goalAchievement);
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

    private void saveScaleAchievements(List<Long> achievableScaleIds, Member member) {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE));
        List<ScaleAchievement> scaleAchievements = achievableScaleIds.stream()
                .map(id -> new ScaleAchievement(member, new Scale(id), now))
                .toList();
        scaleAchievementRepository.saveAll(scaleAchievements);
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
            percentageValuesRepository.save(
                    new ChallengeAchievementRecord(achievement, achievementRecord.percentageValues()));
        }
        return achievement;
    }

    private GoalAchievement handleGoalMode(RunningRecord runningRecord, Integer goalDistance, Integer goalTime) {
        int goalValue = (goalDistance != null) ? goalDistance : goalTime;
        GoalMetricType goalMetricType = (goalDistance != null) ? GoalMetricType.DISTANCE : GoalMetricType.TIME;

        boolean isAchieved = goalMetricType.getActualValue(runningRecord) >= goalValue;

        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, goalMetricType, goalValue, isAchieved);
        return goalAchievementRepository.save(goalAchievement);
    }
}
