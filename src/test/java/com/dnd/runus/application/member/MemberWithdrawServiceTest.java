package com.dnd.runus.application.member;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberWithdrawServiceTest {

    @InjectMocks
    private MemberWithdrawService memberWithdrawService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SocialProfileRepository socialProfileRepository;

    @Mock
    private MemberLevelRepository memberLevelRepository;

    @Mock
    private BadgeAchievementRepository badgeAchievementRepository;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Mock
    private GoalAchievementRepository goalAchievementRepository;

    @Mock
    private ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;

    @Mock
    private ScaleAchievementRepository scaleAchievementRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(MemberRole.USER, "nickname");
    }

    @DisplayName("회원 삭제: 회원이 존재하지 않으면 NotFoundException을 발생하한다.")
    @Test
    public void testDeleteAllDataAboutMember_MemberNotFound() {
        given(memberRepository.findById(member.memberId())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberWithdrawService.deleteAllDataAboutMember(member.memberId()));
    }

    @DisplayName("회원 삭제 : running_record 존재 X")
    @Test
    void testDeleteAllDataAboutMember_NoRunningRecords() {
        // given
        given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
        given(runningRecordRepository.findByMember(member)).willReturn(Collections.emptyList());

        // when
        memberWithdrawService.deleteAllDataAboutMember(member.memberId());

        // then
        then(memberLevelRepository).should().deleteByMemberId(member.memberId());
        then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
        then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
        then(socialProfileRepository).should().deleteByMemberId(member.memberId());
        then(memberRepository).should().deleteById(member.memberId());
    }

    @DisplayName("회원 삭제 : running_record 존재, challenge_achievement 존재 X")
    @Test
    void testDeleteAllDataAboutMember_WithRunningRecords_NoChallengeAchievement() {
        // given
        List<RunningRecord> runningRecords = List.of(new RunningRecord(
                1L,
                member,
                1_100_000,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
        given(runningRecordRepository.findByMember(member)).willReturn(runningRecords);
        given(challengeAchievementRepository.findIdsByRunningRecords(runningRecords))
                .willReturn(Collections.emptyList());

        // when
        memberWithdrawService.deleteAllDataAboutMember(member.memberId());

        // then
        then(memberLevelRepository).should().deleteByMemberId(member.memberId());
        then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
        then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
        then(socialProfileRepository).should().deleteByMemberId(member.memberId());
        then(goalAchievementRepository).should().deleteByRunningRecords(runningRecords);
        then(runningRecordRepository).should().deleteByMemberId(member.memberId());
        then(memberRepository).should().deleteById(member.memberId());
    }

    @DisplayName("회원 삭제 : running_record, challenge_achievement 존재")
    @Test
    void testDeleteAllDataAboutMember_WithRunningRecords_WithChallengeAchievement() {
        // given
        List<RunningRecord> runningRecords = List.of(new RunningRecord(
                1L,
                member,
                1_100_000,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        List<Long> challengeAchievementIds = List.of(1L);

        given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
        given(runningRecordRepository.findByMember(member)).willReturn(runningRecords);
        given(challengeAchievementRepository.findIdsByRunningRecords(runningRecords))
                .willReturn(challengeAchievementIds);

        // when
        memberWithdrawService.deleteAllDataAboutMember(member.memberId());

        // then
        then(memberLevelRepository).should().deleteByMemberId(member.memberId());
        then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
        then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
        then(socialProfileRepository).should().deleteByMemberId(member.memberId());
        then(goalAchievementRepository).should().deleteByRunningRecords(runningRecords);
        then(challengeAchievementPercentageRepository)
                .should()
                .deleteByChallengeAchievementIds(challengeAchievementIds);
        then(challengeAchievementRepository).should().deleteByIds(challengeAchievementIds);
        then(runningRecordRepository).should().deleteByMemberId(member.memberId());
        then(memberRepository).should().deleteById(member.memberId());
    }
}
