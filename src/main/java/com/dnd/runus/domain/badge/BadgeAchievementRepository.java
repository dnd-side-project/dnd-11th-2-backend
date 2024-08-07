package com.dnd.runus.domain.badge;

import com.dnd.runus.domain.member.Member;

public interface BadgeAchievementRepository {
    void deleteByMember(Member member);
}
