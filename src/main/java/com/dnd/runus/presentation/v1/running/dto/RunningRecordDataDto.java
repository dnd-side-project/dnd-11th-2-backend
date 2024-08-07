package com.dnd.runus.presentation.v1.running.dto;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.util.List;

public record RunningRecordDataDto(
        @Schema(description = "평균 페이스", example = "5'30''")
        Pace averagePace,
        @NotBlank
        @Schema(description = "시작 위치", example = "서울시,연남동")
        String location,
        @Schema(description = "멈춘 시간을 제외한 실제로 달린 시간", example = "123:45:56", format = "HH:mm:ss")
        Duration runningTime,
        int distanceMeter,
        double calorie,
        @Size(min = 2, message = "최소 2개의 좌표가 필요합니다.")
        List<Coordinate> route
) {
}
