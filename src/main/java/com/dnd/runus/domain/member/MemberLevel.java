package com.dnd.runus.domain.member;

public record MemberLevel(long memberLevelId, Member member, long levelId, int exp) {
    public record Summary(long memberLevelId, long levelId, int exp) {
        public MemberLevel toMemberLevel(Member member) {
            return new MemberLevel(memberLevelId, member, levelId, exp);
        }
    }
}
