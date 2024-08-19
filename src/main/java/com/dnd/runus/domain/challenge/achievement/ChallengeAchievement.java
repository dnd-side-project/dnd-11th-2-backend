package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.member.Member;
import lombok.Builder;

@Builder
public record ChallengeAchievement(
        Member member, long runningId, long challengeId, ChallengeAchievementRecord record) {}
