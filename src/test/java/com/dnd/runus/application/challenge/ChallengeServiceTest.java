package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @DisplayName("어제 기록이 있는경우 챌린지 리스트 조회 : 챌린지 name에 '어제'값이 포함한 값이 있어야함")
    @Test
    void getChallengesWithYesterdayRecords() {
        // given
        Member member = new Member(MemberRole.USER, "nickname1");
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime startTime = todayMidnight.minusDays(1).plusHours(4);
        OffsetDateTime endTime = startTime.plusMinutes(30);

        RunningRecord record = RunningRecord.builder()
                .member(member)
                .startAt(startTime)
                .endAt(endTime)
                .route(List.of(new Coordinate(123, 34), new Coordinate(123.5, 38)))
                .emoji(RunningEmoji.VERY_GOOD)
                .location("location name")
                .distanceMeter(10)
                .duration(Duration.ofSeconds(1_800))
                .calorie(100)
                .averagePace(new Pace(6, 0))
                .build();

        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(true);

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertTrue(challenges.stream().anyMatch(c -> c.name().contains("어제")));
    }

    @DisplayName("어제 기록이 없는 경우 챌린지 리스트 조회 : 챌린지 name에 '어제'값이 포함한 값이 없어야함")
    @Test
    void getChallengesWithoutYesterdayRecords() {
        // given
        Member member = new Member(MemberRole.USER, "nickname1");
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();

        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(false);

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertTrue(challenges.stream().noneMatch(c -> c.name().contains("어제")));
    }
}
