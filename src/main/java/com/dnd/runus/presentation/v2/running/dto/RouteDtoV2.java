package com.dnd.runus.presentation.v2.running.dto;


import com.dnd.runus.domain.common.CoordinatePoint;
import jakarta.validation.constraints.NotNull;

/**
 * 클라이언트와의 러닝 경로 요청/응답 형식
 * @param start 시작 위치
 * @param end 종료 위치
 */
public record RouteDtoV2(
    @NotNull
    Point start,
    @NotNull
    Point end
) {
    public record Point(double longitude, double latitude) {
        public static Point from(CoordinatePoint point) {
            return new Point(point.longitude(), point.latitude());
        }
    }
}
