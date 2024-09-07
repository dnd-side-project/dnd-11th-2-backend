package com.dnd.runus.domain.scale;

import java.util.List;

public interface ScaleRepository {
    ScaleSummary getSummary();

    List<Long> findAchievableScaleIds(long memberId);
}
