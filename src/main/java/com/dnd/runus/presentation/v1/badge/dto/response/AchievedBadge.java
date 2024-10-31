package com.dnd.runus.presentation.v1.badge.dto.response;

import java.time.LocalDateTime;

public record AchievedBadge(long badgeId, String name, String imageUrl, LocalDateTime achievedAt) {}
