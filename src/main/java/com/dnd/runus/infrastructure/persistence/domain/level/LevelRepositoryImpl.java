package com.dnd.runus.infrastructure.persistence.domain.level;

import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.level.LevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.level.JpaLevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.level.entity.LevelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LevelRepositoryImpl implements LevelRepository {
    private final JpaLevelRepository jpaLevelRepository;

    @Override
    public Optional<Level> findById(long id) {
        return jpaLevelRepository.findById(id).map(LevelEntity::toDomain);
    }

    @Override
    public Level save(Level level) {
        return jpaLevelRepository.save(LevelEntity.from(level)).toDomain();
    }
}
