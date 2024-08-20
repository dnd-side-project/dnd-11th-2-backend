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
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
