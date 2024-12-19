package com.dnd.runus.application.running;

import com.dnd.runus.application.running.event.RunningRecordAddedEvent;
import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.presentation.v1.running.dto.WeeklyRunningRatingDto;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordWeeklySummaryType;
import com.dnd.runus.presentation.v1.running.dto.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.dnd.runus.global.constant.MetricsConversionFactor.METERS_IN_A_KILOMETER;
import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_HOUR;
import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;

@Service
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;
    private final ChallengeAchievementPercentageRepository percentageValuesRepository;
    private final GoalAchievementRepository goalAchievementRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final ZoneOffset defaultZoneOffset;

    private static final OffsetDateTime BASE_TIME = Instant.EPOCH.atOffset(ZoneOffset.UTC);

    public RunningRecordService(
            RunningRecordRepository runningRecordRepository,
            MemberRepository memberRepository,
            ChallengeRepository challengeRepository,
            ChallengeAchievementRepository challengeAchievementRepository,
            ChallengeAchievementPercentageRepository percentageValuesRepository,
            GoalAchievementRepository goalAchievementRepository,
            ApplicationEventPublisher eventPublisher,
            @Value("${app.default-zone-offset}") ZoneOffset defaultZoneOffset) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberRepository = memberRepository;
        this.challengeRepository = challengeRepository;
        this.challengeAchievementRepository = challengeAchievementRepository;
        this.percentageValuesRepository = percentageValuesRepository;
        this.goalAchievementRepository = goalAchievementRepository;
        this.eventPublisher = eventPublisher;
        this.defaultZoneOffset = defaultZoneOffset;
    }

    @Transactional(readOnly = true)
    public RunningRecordQueryResponse getRunningRecord(long memberId, long runningRecordId) {
        RunningRecord runningRecord = runningRecordRepository
                .findById(runningRecordId)
                .filter(r -> r.member().memberId() == memberId)
                .orElseThrow(() -> new NotFoundException(RunningRecord.class, runningRecordId));

        ChallengeAchievement challengeAchievement = challengeAchievementRepository
                .findByRunningRecordId(runningRecordId)
                .map(status -> new ChallengeAchievement(
                        status.challengeAchievementId(), status.challenge(), runningRecord, status.isSuccess()))
                .orElse(null);

        GoalAchievement goalAchievement = (challengeAchievement == null)
                ? goalAchievementRepository
                        .findByRunningRecordId(runningRecordId)
                        .orElse(null)
                : null;

        return RunningRecordQueryResponse.of(runningRecord, challengeAchievement, goalAchievement);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getRunningRecordDates(long memberId, int year, int month) {
        OffsetDateTime from = LocalDate.of(year, month, 1).atStartOfDay().atOffset(defaultZoneOffset);
        OffsetDateTime to = from.plusMonths(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);

        return records.stream()
                .map(RunningRecord::startAt)
                .map(ZonedDateTime::toLocalDate)
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
            weekSummaries = runningRecordRepository.findDailyDistancesMeterWithDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            avgValue = runningRecordRepository.findAvgDistanceMeterByMemberIdWithDateRange(
                    memberId, startWeekDate.minusDays(7), nextOfEndWeekDate.minusDays(7));
            conversionFactor = METERS_IN_A_KILOMETER;
        } else {
            weekSummaries = runningRecordRepository.findDailyDurationsSecWithDateRange(
                    memberId, startWeekDate, nextOfEndWeekDate);
            avgValue = runningRecordRepository.findAvgDurationSecByMemberIdWithDateRange(
                    memberId, startWeekDate.minusDays(7), nextOfEndWeekDate.minusDays(7));
            conversionFactor = SECONDS_PER_HOUR;
        }

        List<WeeklyRunningRatingDto> weeklyValues = Arrays.stream(DayOfWeek.values())
                .map(day -> new WeeklyRunningRatingDto(
                        day.getDisplayName(TextStyle.SHORT, Locale.KOREAN), 0.0)) // 초기값으로 0.0 설정
                .collect(Collectors.toList());

        // weeklyValues에 값 set
        for (DailyRunningRecordSummary summary : weekSummaries) {
            DayOfWeek dayOfWeek = summary.date().getDayOfWeek();
            weeklyValues.set(
                    dayOfWeek.getValue() - 1,
                    new WeeklyRunningRatingDto(
                            dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                            summary.sumValue() / conversionFactor));
        }

        return new RunningRecordWeeklySummaryResponse(
                startWeekDate.toLocalDate(),
                startWeekDate.plusDays(6).toLocalDate(),
                weeklyValues,
                avgValue / conversionFactor);
    }

    @Transactional(readOnly = true)
    public RunningRecordMonthlySummaryResponse getMonthlyRunningSummery(long memberId) {

        OffsetDateTime startDateOfMonth =
                OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE)).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime startDateOfNextMonth = startDateOfMonth.plusMonths(1);
        return RunningRecordMonthlySummaryResponse.builder()
                .month(startDateOfMonth.getMonthValue())
                .monthlyTotalMeter(runningRecordRepository.findTotalDistanceMeterByMemberIdWithRangeDate(
                        memberId, startDateOfMonth, startDateOfNextMonth))
                .build();
    }

    @Transactional
    public RunningRecordAddResultResponseV1 addRunningRecordV1(long memberId, RunningRecordRequest request) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        CoordinatePoint emptyCoordinate = new CoordinatePoint(0, 0, 0);
        List<CoordinatePoint> route = List.of(emptyCoordinate, emptyCoordinate);

        RunningRecord record = runningRecordRepository.save(RunningRecord.builder()
                .member(member)
                .startAt(request.startAt().atZone(defaultZoneOffset))
                .endAt(request.endAt().atZone(defaultZoneOffset))
                .emoji(request.emotion())
                .startLocation(request.startLocation())
                .endLocation(request.endLocation())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(Pace.from(
                        request.runningData().distanceMeter(),
                        request.runningData().runningTime()))
                .route(route)
                .build());

        OffsetDateTime now = OffsetDateTime.now();
        int totalDistance = runningRecordRepository.findTotalDistanceMeterByMemberId(memberId);
        Duration totalDuration = runningRecordRepository.findTotalDurationByMemberId(memberId, BASE_TIME, now);

        eventPublisher.publishEvent(new RunningRecordAddedEvent(member, record, totalDistance, totalDuration));

        switch (request.achievementMode()) {
            case CHALLENGE -> {
                ChallengeAchievement challengeAchievement =
                        handleChallengeMode(request.challengeId(), memberId, record);
                return RunningRecordAddResultResponseV1.of(record, challengeAchievement);
            }
            case GOAL -> {
                GoalAchievement goalAchievement = handleGoalMode(record, request.goalDistance(), request.goalTime());
                return RunningRecordAddResultResponseV1.of(record, goalAchievement);
            }
        }
        return RunningRecordAddResultResponseV1.from(record);
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
