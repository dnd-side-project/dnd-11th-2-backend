package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import lombok.Builder;

@Builder
public record ChallengeAchievement(
        Member member, RunningRecord runningRecord, long challengeId, ChallengeAchievementRecord record) {}
