package com.dnd.runus.domain.common;

/**
 * @param longitude 경도
 * @param latitude  위도
 * @param altitude  고도
 */
public record CoordinatePoint(double longitude, double latitude, double altitude) {

    public CoordinatePoint(double longitude, double latitude) {
        this(longitude, latitude, Double.NaN);
    }

    /**
     * null Island(longitude : 0, latitude: 0, altitude:0 인지점)을 확인
     */
    public boolean isNullIsland() {
        return (this.longitude == 0 && this.latitude == 0);
    }
}
