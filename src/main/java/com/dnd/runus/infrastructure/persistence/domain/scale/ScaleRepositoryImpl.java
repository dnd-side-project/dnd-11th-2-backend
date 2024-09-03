package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.domain.scale.ScaleSummary;
import com.dnd.runus.infrastructure.persistence.jooq.scale.JooqScaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScaleRepositoryImpl implements ScaleRepository {

    private final JooqScaleRepository jooqScaleRepository;

    @Override
    public ScaleSummary getSummary() {
        return jooqScaleRepository.getSummary();
    }
}
