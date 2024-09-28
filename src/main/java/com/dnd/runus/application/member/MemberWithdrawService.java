package com.dnd.runus.application.member;

import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberRepository memberRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;
    private final GoalAchievementRepository goalAchievementRepository;
    private final ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;
    private final ScaleAchievementRepository scaleAchievementRepository;

    @Transactional
    public void deleteAllDataAboutMember(long memberId) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        memberLevelRepository.deleteByMemberId(member.memberId());
        badgeAchievementRepository.deleteByMemberId(member.memberId());
        scaleAchievementRepository.deleteByMemberId(member.memberId());
        socialProfileRepository.deleteByMemberId(member.memberId());

        // running_record 조회
        List<RunningRecord> runningRecords = runningRecordRepository.findByMember(member);
        if (runningRecords.isEmpty()) {
            // running_record가 없으면 멤버 삭제 후 리턴
            memberRepository.deleteById(member.memberId());
            return;
        }

        // goal_achievement 삭제
        goalAchievementRepository.deleteByRunningRecords(runningRecords);

        // running_record로 challenge_achievement 조회
        List<Long> challengeAchievementIds = challengeAchievementRepository.findIdsByRunningRecords(runningRecords);
        // challenge_achievement가 존재하면 challenge_achievement_percentage, challenge_achievement 삭제
        if (!challengeAchievementIds.isEmpty()) {
            challengeAchievementPercentageRepository.deleteByChallengeAchievementIds(challengeAchievementIds);
            challengeAchievementRepository.deleteByIds(challengeAchievementIds);
        }

        // running_record 삭제
        runningRecordRepository.deleteByMemberId(member.memberId());

        memberRepository.deleteById(member.memberId());

        log.info("멤버 삭제 완료: memberId={}", member.memberId());
    }
}
