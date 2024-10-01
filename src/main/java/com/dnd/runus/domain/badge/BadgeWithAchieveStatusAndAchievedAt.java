package com.dnd.runus.domain.badge;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record BadgeWithAchieveStatusAndAchievedAt(@NotNull Badge badge, boolean isAchieved, OffsetDateTime achievedAt) {
    public BadgeWithAchieveStatusAndAchievedAt(Badge badge, OffsetDateTime achievedAt) {
        this(badge, achievedAt != null, achievedAt);
    }
}
