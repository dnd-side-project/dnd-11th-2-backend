package com.dnd.runus.domain.member;

public record MemberLevel(long memberLevelId, Member member, long levelId, int exp) {
    public MemberLevel(Member member) {
        this(0, member, 1, 0);
    }
}
