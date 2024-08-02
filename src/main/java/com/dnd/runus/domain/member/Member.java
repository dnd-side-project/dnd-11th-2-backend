package com.dnd.runus.domain.member;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.level.Level;
import com.dnd.runus.global.constant.MemberRole;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record Member(
        long memberId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        MemberRole role,
        String nickname,
        Badge mainBadge,
        int weightKg,
        Level level,
        int currentExp) {}
