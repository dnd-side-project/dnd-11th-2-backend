package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;
import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
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
                OffsetDateTime.now(),
                OffsetDateTime.now(),
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
        OffsetDateTime dayBeforeYesterday = yesterday.minusHours(1);
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
        OffsetDateTime dayBeforeYesterday = yesterday.minusHours(1);
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
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
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
                    OffsetDateTime.now(),
                    OffsetDateTime.now(),
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
}
