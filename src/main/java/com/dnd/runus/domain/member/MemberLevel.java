package com.dnd.runus.domain.member;

import com.dnd.runus.domain.level.Level;

public record MemberLevel(long memberLevelId, Member member, long levelId, int exp) {
    public MemberLevel(Member member) {
        this(0, member, 1, 0);
    }

    public record Current(Level level, int currentExp) {}
}
