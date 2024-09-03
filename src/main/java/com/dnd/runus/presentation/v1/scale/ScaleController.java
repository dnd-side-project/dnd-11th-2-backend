package com.dnd.runus.presentation.v1.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.presentation.v1.scale.dto.ScaleSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지구 한 바퀴")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scale")
public class ScaleController {

    private final ScaleService scaleService;

    @Operation(
            summary = "지구 한 바퀴 코스 서머리",
            description =
                    """
                지구 한 바퀴 코스 서머리를 조회 합니다.<br>
                [달성 기록] - [런어스랑 지구한바퀴 달리기]의 전체 코스, 런어스 총 거리, 지구 한 바퀴의 값을 리턴합니다.
                """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("course-summary")
    public ScaleSummaryResponse getScaleSummary() {
        return scaleService.getSummary();
    }
}
