package com.dnd.runus.domain.level;

import java.util.Optional;

public interface LevelRepository {
    Optional<Level> findById(long id);

    Level save(Level level);
}
