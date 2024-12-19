package com.dnd.runus.domain.common;

/**
 * @param longitude 경도
 * @param latitude 위도
 * @param altitude 고도
 */
public record CoordinatePoint(double longitude, double latitude, double altitude) {
    public CoordinatePoint(double longitude, double latitude) {
        this(longitude, latitude, Double.NaN);
    }
}
