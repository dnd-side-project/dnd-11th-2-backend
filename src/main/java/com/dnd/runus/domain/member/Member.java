package com.dnd.runus.domain.member;

import com.dnd.runus.global.constant.MemberRole;

import java.time.OffsetDateTime;

public record Member(
        long memberId, MemberRole role, String nickname, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    public Member(MemberRole role, String nickname) {
        this(0, role, nickname, null, null);
    }
}
