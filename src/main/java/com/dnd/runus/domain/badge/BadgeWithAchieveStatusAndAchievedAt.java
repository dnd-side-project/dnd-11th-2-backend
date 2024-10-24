package com.dnd.runus.domain.badge;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record BadgeWithAchieveStatusAndAchievedAt(@NotNull Badge badge, boolean isAchieved, LocalDateTime achievedAt) {
    public BadgeWithAchieveStatusAndAchievedAt(Badge badge, OffsetDateTime achievedAt) {
        this(badge, achievedAt != null, achievedAt != null ? achievedAt.toLocalDateTime() : null);
    }
}
