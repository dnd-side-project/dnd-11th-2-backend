package com.dnd.runus.domain.member;

public record MemberLevel(long memberLevelId, Member member, long levelId, int exp) {}
