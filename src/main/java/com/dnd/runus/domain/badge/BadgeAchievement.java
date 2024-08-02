package com.dnd.runus.domain.badge;

import com.dnd.runus.domain.member.Member;

import java.time.OffsetDateTime;

public record BadgeAchievement(Badge badge, Member member, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
