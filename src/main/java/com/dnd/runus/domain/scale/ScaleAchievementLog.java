package com.dnd.runus.domain.scale;

import io.micrometer.common.lang.Nullable;

import java.time.OffsetDateTime;

public record ScaleAchievementLog(Scale scale, @Nullable OffsetDateTime achievedDate) {}
