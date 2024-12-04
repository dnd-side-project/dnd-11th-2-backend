package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.*;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private OffsetDateTime todayMidnight;
    private Member member;

    @BeforeEach
    void setUp() {
        todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        member = new Member(MemberRole.USER, "nickname");
    }

    @DisplayName("어제 기록이 있는경우 챌린지 리스트 조회 : 챌린지 리스트 크기가 2이어야함")
    @Test
    void getChallengesWithYesterdayRecords() {
        // given
        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(true);

        given(challengeRepository.findAllChallenges())
                .willReturn(List.of(
                        new Challenge(1L, "어제보다 1km더 뛰기", "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY),
                        new Challenge(2L, "어제보다 5분 더 뛰기", "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY),
                        new Challenge(3L, "어제보다 평균 페이스 10초 빠르게", "imageUrl", true, ChallengeType.DEFEAT_YESTERDAY),
                        new Challenge(4L, "오늘 5km 뛰기", "imageUrl", true, ChallengeType.TODAY),
                        new Challenge(5L, "오늘 30분 뛰기", "imageUrl", true, ChallengeType.TODAY),
                        new Challenge(6L, "1km 6분안에 뛰기", "imageUrl", true, ChallengeType.DISTANCE_IN_TIME)));

        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertThat(challenges.size()).isEqualTo(2);
    }

    @DisplayName("어제 기록이 없는 경우 챌린지 리스트 조회 : 챌린지 리스트 크기가 2이어야함")
    @Test
    void getChallengesWithoutYesterdayRecords() {
        // given
        given(runningRecordRepository.hasByMemberIdAndStartAtBetween(
                        member.memberId(), todayMidnight.minusDays(1), todayMidnight))
                .willReturn(false);

        given(challengeRepository.findAllIsNotDefeatYesterday())
                .willReturn(List.of(
                        new Challenge(4L, "오늘 5km 뛰기", "imageUrl", true, ChallengeType.TODAY),
                        new Challenge(5L, "오늘 30분 뛰기", "imageUrl", true, ChallengeType.TODAY),
                        new Challenge(6L, "1km 6분안에 뛰기", "imageUrl", true, ChallengeType.DISTANCE_IN_TIME)));
        // when
        List<ChallengesResponse> challenges = challengeService.getChallenges(member.memberId());

        // then
        assertThat(challenges.size()).isEqualTo(2);
    }
}
