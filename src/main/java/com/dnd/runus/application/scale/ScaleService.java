package com.dnd.runus.application.scale;

import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.domain.scale.ScaleSummary;
import com.dnd.runus.presentation.v1.scale.dto.ScaleSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dnd.runus.global.constant.ScaleConstant.DISTANCE_KM_AROUND_THE_EARTH;

@Service
@RequiredArgsConstructor
public class ScaleService {

    private final ScaleRepository scaleRepository;

    public ScaleSummaryResponse getSummary() {
        ScaleSummary summary = scaleRepository.getSummary();

        return new ScaleSummaryResponse(
                summary.totalCourseCnt(), summary.totalCourseDistanceKm(), DISTANCE_KM_AROUND_THE_EARTH);
    }
}
