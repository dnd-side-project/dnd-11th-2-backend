package com.dnd.runus.domain.scale;

import java.util.List;

public interface ScaleAchievementRepository {
    List<ScaleAchievementLog> findScaleAchievementLogs(long memberId);
}
