package com.dnd.runus.domain.scale;

import java.util.List;

public interface ScaleRepository {
    List<Long> findAchievableScaleIds(long memberId);
}
