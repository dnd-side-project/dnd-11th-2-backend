package com.dnd.runus.infrastructure.persistence.domain;

import com.dnd.runus.domain.common.CoordinatePoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.List;
import java.util.stream.Stream;

import static com.dnd.runus.global.constant.GeometryConstant.SRID;
import static org.locationtech.jts.geom.PrecisionModel.FLOATING;

public final class GeometryMapper {
    GeometryMapper() {}

    public static CoordinatePoint toDomain(Coordinate coordinate) {
        return new CoordinatePoint(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    }

    public static List<CoordinatePoint> toDomain(LineString lineString) {
        return Stream.of(lineString.getCoordinates())
                .map(GeometryMapper::toDomain)
                .toList();
    }

    public static LineString toLineString(List<CoordinatePoint> coordinates) {
        Coordinate[] geoCoordinates = coordinates.stream()
                .map(coordinate -> new Coordinate(coordinate.longitude(), coordinate.latitude(), coordinate.altitude()))
                .toArray(Coordinate[]::new);
        return GeometryFactoryHolder.INSTANCE.createLineString(geoCoordinates);
    }

    private static class GeometryFactoryHolder {
        private static final GeometryFactory INSTANCE = new GeometryFactory(new PrecisionModel(FLOATING), SRID);
    }
}
