package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.DailyRunningRecordSummary;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;
import java.time.*;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;
import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
class RunningRecordRepositoryImplTest {

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void beforeEach() {
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @DisplayName("MemberId로 runningRecord 삭제")
    @Test
    void deleteByMemberId() {
        // given
        RunningRecord runningRecord = new RunningRecord(
                0,
                savedMember,
                1,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.BAD);
        RunningRecord savedRunningRecord = runningRecordRepository.save(runningRecord);

        // when
        runningRecordRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(
                runningRecordRepository.findById(savedRunningRecord.runningId()).isPresent());
    }

    @DisplayName("어제 러닝기록을 memberId로 select")
    @Test
    void getYesterdayRunningRecord() {
        // given
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);
        ZonedDateTime dayBeforeYesterday = yesterday.minusHours(1).toZonedDateTime();
        // 그제:2, 어제:4, 오늘:1개
        for (int i = 0; i < 7; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    dayBeforeYesterday.plusHours(i * i).plusMinutes(1),
                    dayBeforeYesterday.plusHours(i * i).plusMinutes(30),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        }

        // when & then
        assertThat(runningRecordRepository
                        .findByMemberIdAndStartAtBetween(savedMember.memberId(), yesterday, todayMidnight)
                        .size())
                .isEqualTo(4);
    }

    @DisplayName("어제 러닝기록, memberId의 러닝 기록이 존재하는지 확인")
    @Test
    void hasYesterdayRunningRecord() {
        // given
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);
        ZonedDateTime dayBeforeYesterday = yesterday.minusHours(1).toZonedDateTime();
        // 그제:2, 어제:4, 오늘:1개
        for (int i = 0; i < 7; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    dayBeforeYesterday.plusHours(i * i).plusMinutes(1),
                    dayBeforeYesterday.plusHours(i * i).plusMinutes(30),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        }

        // when & then
        assertTrue(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                savedMember.memberId(), yesterday, todayMidnight));
    }

    @DisplayName("이번달 달린 기록 조회: 러닝 기록이 없을 시 0을 리턴")
    @Test
    void getTotalDistanceNoRunningRecord() {
        // given
        OffsetDateTime startDateOfMonth =
                OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE)).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime startDateOfNextMonth = startDateOfMonth.plusMonths(1);

        // when
        int totalDistanceMeterByMemberId = runningRecordRepository.findTotalDistanceMeterByMemberId(
                savedMember.memberId(), startDateOfMonth, startDateOfNextMonth);

        // than
        assertThat(totalDistanceMeterByMemberId).isEqualTo(0);
    }

    @DisplayName("이번달 달린 기록 조회: 러닝 기록이 존재하면 sum한 값을 리턴")
    @Test
    void getTotalDistanceWithRunningRecords() {
        // given
        ZonedDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID).atStartOfDay(SERVER_TIMEZONE_ID);

        // 러닝 기록 저장
        for (int i = 0; i < 3; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    todayMidnight,
                    todayMidnight,
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        }
        OffsetDateTime startDateOfMonth =
                OffsetDateTime.now(ZoneId.of(SERVER_TIMEZONE)).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime startDateOfNextMonth = startDateOfMonth.plusMonths(1);

        // when
        int totalDistanceMeterByMemberId = runningRecordRepository.findTotalDistanceMeterByMemberId(
                savedMember.memberId(), startDateOfMonth, startDateOfNextMonth);

        // than
        assertThat(totalDistanceMeterByMemberId).isEqualTo(3000);
    }

    @DisplayName("러닝 기록 조회 : memberId의 러닝 기록을 조회")
    @Test
    void getAllRunningRecordsByMemberId() {
        // given
        for (int i = 0; i < 2; i++) {
            runningRecordRepository.save(new RunningRecord(
                    0,
                    savedMember,
                    5000,
                    Duration.ofHours(1),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now(),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO));
        }

        // when
        List<RunningRecord> results = runningRecordRepository.findByMember(savedMember);

        // then
        assertNotNull(results);
        assertThat(results.size()).isEqualTo(2);
    }

    @DisplayName("러닝 주간 서머리 조회(거리) : 러닝 데이터가 없을 경우")
    @Test
    void getWeeklyDistanceSummary_WithOutRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        // when
        List<DailyRunningRecordSummary> result = runningRecordRepository.findDailyDistancesMeterByDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("러닝 주간 서머리 조회(거리) : 러닝 데이터 있을 경우, 해당 요일에 러닝 데이터는 sum한 값이 리턴된다.")
    @Test
    void getWeeklyDistanceSummary_WithRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        for (int i = 0; i < 2; i++) {
            runningRecordRepository.save(new RunningRecord(
                    0,
                    savedMember,
                    5000,
                    Duration.ofHours(1),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now(),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO));
        }

        // when
        List<DailyRunningRecordSummary> result = runningRecordRepository.findDailyDistancesMeterByDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).sumValue()).isEqualTo(10_000);
    }

    @DisplayName("러닝 주간 서머리 조회(시간) : 러닝 데이터가 없을 경우")
    @Test
    void getWeeklyDurationSummary_WithOutRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        // when
        List<DailyRunningRecordSummary> result = runningRecordRepository.findDailyDurationsSecByDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("러닝 주간 서머리 조회(시간) : 러닝 데이터 있을 경우, 해당 요일에 러닝 데이터는 sum한 값이 리턴된다.")
    @Test
    void getWeeklyDurationSummary_WithRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        for (int i = 0; i < 2; i++) {
            runningRecordRepository.save(new RunningRecord(
                    0,
                    savedMember,
                    5000,
                    Duration.ofHours(1),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now(),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO));
        }

        // when
        List<DailyRunningRecordSummary> result = runningRecordRepository.findDailyDurationsSecByDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).sumValue()).isEqualTo(Duration.ofHours(2).toSeconds());
    }

    @DisplayName("러닝 기간별 평균 거리 조회: 기간내 러닝 데이터가 없으면 0을 리턴한다.")
    @Test
    void getAvgDistance_WithOutRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        // when
        int avgResult = runningRecordRepository.findAvgDistanceMeterByMemberIdAndDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertThat(avgResult).isEqualTo(0);
    }

    @DisplayName("러닝 기간별 평균 거리 조회: 기간의 경계값 위주의 러닝데이터를 확인한다.")
    @Test
    void getAvgDistance_WithRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        ZonedDateTime today = ZonedDateTime.now(defaultZoneOffset).toLocalDate().atStartOfDay(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        ZonedDateTime startDate = today.minusDays(day);
        ZonedDateTime nextOfDndDate = startDate.plusDays(7);

        // 러닝 기록 경계 값 위주로 저장(시작일 전날 1개, 시작일 1개, 종료일자로 1개, nextOfDndDate로 1개)
        // 시작일 전날 - 1km
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                1000,
                Duration.ofHours(1),
                1,
                new Pace(5, 11),
                startDate.minusDays(1),
                startDate.minusDays(1).plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 시작일 - 2km
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                2000,
                Duration.ofHours(2),
                1,
                new Pace(5, 11),
                startDate,
                startDate.plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 종료일 - 2km
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                2000,
                Duration.ofHours(2),
                1,
                new Pace(5, 11),
                nextOfDndDate.minusDays(1),
                nextOfDndDate.minusDays(1).plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 종료일 - 1km
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                1000,
                Duration.ofHours(1),
                1,
                new Pace(5, 11),
                nextOfDndDate,
                nextOfDndDate.plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        // when
        int avgResult = runningRecordRepository.findAvgDistanceMeterByMemberIdAndDateRange(
                savedMember.memberId(), startDate.toOffsetDateTime(), nextOfDndDate.toOffsetDateTime());

        // then
        assertThat(avgResult).isEqualTo(2000);
    }

    @DisplayName("러닝 기간별 평균 시간 조회: 기간 내 러닝 데이터가 없는 경우 0을 리턴한다.")
    @Test
    void getAvgDuration_WithOutRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        OffsetDateTime startDate = today.minusDays(day);
        OffsetDateTime nextOfDndDate = startDate.plusDays(7);

        // when
        int avgResult = runningRecordRepository.findAvgDurationSecByMemberIdAndDateRange(
                savedMember.memberId(), startDate, nextOfDndDate);

        // then
        assertThat(avgResult).isEqualTo(0);
    }

    @DisplayName("러닝 기간별 평균 시간 조회: 기간의 경계값 위주의 러닝데이터를 확인한다.")
    @Test
    void getAvgDuration_WithRunningRecords() {
        // given
        ZoneOffset defaultZoneOffset = ZoneOffset.of("+09:00");
        ZonedDateTime today = ZonedDateTime.now(defaultZoneOffset).toLocalDate().atStartOfDay(defaultZoneOffset);

        int day = today.get(DAY_OF_WEEK) - 1;
        ZonedDateTime startDate = today.minusDays(day);
        ZonedDateTime nextOfDndDate = startDate.plusDays(7);

        // 러닝 기록 경계 값 위주로 저장(시작일 전날 1개, 시작일 1개, 종료일자로 1개, nextOfDndDate로 1개)
        // 시작일 전날 - 1시간
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                5000,
                Duration.ofHours(1),
                1,
                new Pace(5, 11),
                startDate.minusDays(1),
                startDate.minusDays(1).plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 시작일 - 2시간
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                5000,
                Duration.ofHours(2),
                1,
                new Pace(5, 11),
                startDate,
                startDate.plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 종료일 - 2시간
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                5000,
                Duration.ofHours(2),
                1,
                new Pace(5, 11),
                nextOfDndDate.minusDays(1),
                nextOfDndDate.minusDays(1).plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // 종료일 - 1시간
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                5000,
                Duration.ofHours(1),
                1,
                new Pace(5, 11),
                nextOfDndDate,
                nextOfDndDate.plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        // when
        int avgResult = runningRecordRepository.findAvgDurationSecByMemberIdAndDateRange(
                savedMember.memberId(), startDate.toOffsetDateTime(), nextOfDndDate.toOffsetDateTime());

        // then
        assertThat(avgResult).isEqualTo(Duration.ofHours(2).toSeconds());
    }
}
