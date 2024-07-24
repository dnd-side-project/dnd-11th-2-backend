package com.dnd.runus.domain.runing.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class RoutePoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "running_record_id", nullable = false)
    private RunningRecord runningRecord;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point point;

    @Column(nullable = false)
    private Instant recordAt;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Builder
    public RoutePoint(RunningRecord runningRecord, Point point, Instant recordAt, Integer sequenceNumber) {
        this.runningRecord = runningRecord;
        this.point = point;
        this.recordAt = recordAt;
        this.sequenceNumber = sequenceNumber;
    }
}
