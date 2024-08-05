package com.dnd.runus.domain.member;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.global.constant.MemberRole;

import java.time.OffsetDateTime;

public record Member(
        long memberId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        MemberRole role,
        String nickname,
        Badge mainBadge) {
    public Member(MemberRole role, String nickname) {
        this(0, null, null, role, nickname, null);
    }
}
