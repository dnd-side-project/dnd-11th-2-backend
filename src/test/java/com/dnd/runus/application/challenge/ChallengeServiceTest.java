package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeRepository challengeRepository;

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

        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(true);
        given(challengeRepository.getChallenges(true))
                .willReturn(Arrays.asList(
                        new Challenge(1L, "어제보다 1km더 뛰기", "8분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(2L, "어제보다 5분 더 뛰기", "5분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(
                                3L, "어제보다 평균 페이스 10초 빠르게", "0분", "imageUrl", ChallengeType.DEFEAT_YESTERDAY, null),
                        new Challenge(4L, "오늘 5km 뛰기", "0분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(5L, "오늘 30분 동안 뛰기", "30분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(6L, "1km 6분안에 뛰기", "30분", "imageUrl", ChallengeType.DISTANCE_IN_TIME, null)));

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
        given(challengeRepository.getChallenges(false))
                .willReturn(Arrays.asList(
                        new Challenge(4L, "오늘 5km 뛰기", "0분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(5L, "오늘 30분 동안 뛰기", "30분", "imageUrl", ChallengeType.TODAY, null),
                        new Challenge(6L, "1km 6분안에 뛰기", "30분", "imageUrl", ChallengeType.DISTANCE_IN_TIME, null)));

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertTrue(challenges.stream().noneMatch(c -> c.name().contains("어제")));
    }
}
