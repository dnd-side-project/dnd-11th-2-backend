package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jooq.scale.JooqScaleAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScaleAchievementRepositoryImpl implements ScaleAchievementRepository {

    private final JooqScaleAchievementRepository jooqScaleAchievementRepository;

    @Override
    public List<ScaleAchievementLog> findScaleAchievementLogs(long memberId) {
        return jooqScaleAchievementRepository.findScaleAchievementLogs(memberId);
    }
}
