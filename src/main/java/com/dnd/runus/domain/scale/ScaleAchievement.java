package com.dnd.runus.domain.scale;

import com.dnd.runus.domain.member.Member;

import java.time.OffsetDateTime;

public record ScaleAchievement(Member member, Scale scale, OffsetDateTime achievedDate) {}
