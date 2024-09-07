package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jooq.scale.JooqScaleAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.scale.JpaScaleAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScaleAchievementRepositoryImpl implements ScaleAchievementRepository {

    private final JooqScaleAchievementRepository jooqScaleAchievementRepository;
    private final JpaScaleAchievementRepository jpaScaleAchievementRepository;

    @Override
    public List<ScaleAchievementLog> findScaleAchievementLogs(long memberId) {
        return jooqScaleAchievementRepository.findScaleAchievementLogs(memberId);
    }

    @Override
    public List<ScaleAchievement> saveAll(List<ScaleAchievement> scaleAchievements) {
        return jpaScaleAchievementRepository
                .saveAll(scaleAchievements.stream()
                        .map(ScaleAchievementEntity::from)
                        .toList())
                .stream()
                .map(ScaleAchievementEntity::toDomain)
                .toList();
    }
}
