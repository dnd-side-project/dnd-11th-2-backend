package com.dnd.runus.infrastructure.persistence.jpa.running.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.running.RunningEmoji;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static com.dnd.runus.global.constant.GeometryConstant.SRID;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;
import static org.locationtech.jts.geom.PrecisionModel.FLOATING;

@Getter
@Entity(name = "running_record")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class RunningRecordEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private MemberEntity memberEntity;

    @NotNull
    private Integer distanceMeter;

    @NotNull
    private Integer durationSeconds;

    @NotNull
    private Double calorie;

    @NotNull
    private Double averagePace;

    @NotNull
    private OffsetDateTime startAt;

    @NotNull
    private OffsetDateTime endAt;

    @Column(columnDefinition = "geometry(LineString, " + SRID + ")")
    private LineString route;

    @NotNull
    private String location;

    @NotNull
    private Long emojiId;

    public static RunningRecordEntity from(RunningRecord runningRecord) {
        org.locationtech.jts.geom.Coordinate[] coordinates = runningRecord.route().stream()
                .map(coordinate ->
                        new org.locationtech.jts.geom.Coordinate(coordinate.longitude(), coordinate.latitude()))
                .toArray(org.locationtech.jts.geom.Coordinate[]::new);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(FLOATING), SRID);
        LineString route = geometryFactory.createLineString(coordinates);

        return RunningRecordEntity.builder()
                .id(runningRecord.runningId())
                .memberEntity(MemberEntity.from(runningRecord.member()))
                .distanceMeter(runningRecord.distanceMeter())
                .durationSeconds(runningRecord.durationSeconds())
                .calorie(runningRecord.calorie())
                .averagePace(runningRecord.averagePace())
                .startAt(runningRecord.startAt())
                .endAt(runningRecord.endAt())
                .route(route)
                .location(runningRecord.location())
                .emojiId(runningRecord.emoji().emojiId())
                .build();
    }

    public RunningRecord toDomain() {
        List<Coordinate> coordinates = Arrays.stream(route.getCoordinates())
                .map(coordinate -> new Coordinate(coordinate.x, coordinate.y, coordinate.z))
                .toList();

        return RunningRecord.builder()
                .runningId(id)
                .member(memberEntity.toDomain())
                .distanceMeter(distanceMeter)
                .durationSeconds(durationSeconds)
                .calorie(calorie)
                .averagePace(averagePace)
                .startAt(startAt)
                .endAt(endAt)
                .route(coordinates)
                .location(location)
                .emoji(new RunningEmoji(emojiId, null))
                .build();
    }
}
