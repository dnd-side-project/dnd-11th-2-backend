package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.infrastructure.persistence.jooq.scale.JooqScaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScaleRepositoryImpl implements ScaleRepository {

    private final JooqScaleRepository jooqScaleRepository;

    @Override
    public List<Long> findAchievableScaleIds(long memberId) {
        return jooqScaleRepository.findAchievableScaleIds(memberId);
    }
}
