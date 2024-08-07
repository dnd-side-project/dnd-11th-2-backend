package com.dnd.runus.infrastructure.persistence.jpa.running.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.domain.GeometryMapper;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.Duration;
import java.time.OffsetDateTime;

import static com.dnd.runus.global.constant.GeometryConstant.SRID;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
    private MemberEntity member;

    @NotNull
    private Integer distanceMeter;

    @NotNull
    private Integer durationSeconds;

    @NotNull
    private Double calorie;

    @NotNull
    private Integer averagePace;

    @NotNull
    private OffsetDateTime startAt;

    @NotNull
    private OffsetDateTime endAt;

    @Column(columnDefinition = "geometry(LineStringZ, " + SRID + ")")
    private LineString route;

    @NotNull
    private String location;

    @NotNull
    @Enumerated(STRING)
    private RunningEmoji emoji;

    public static RunningRecordEntity from(RunningRecord runningRecord) {
        return RunningRecordEntity.builder()
                .id(runningRecord.runningId())
                .member(MemberEntity.from(runningRecord.member()))
                .distanceMeter(runningRecord.distanceMeter())
                .durationSeconds((int) runningRecord.duration().toSeconds())
                .calorie(runningRecord.calorie())
                .averagePace(runningRecord.averagePace().toSeconds())
                .startAt(runningRecord.startAt())
                .endAt(runningRecord.endAt())
                .route(GeometryMapper.toLineString(runningRecord.route()))
                .location(runningRecord.location())
                .emoji(runningRecord.emoji())
                .build();
    }

    public RunningRecord toDomain() {
        return RunningRecord.builder()
                .runningId(id)
                .member(member.toDomain())
                .distanceMeter(distanceMeter)
                .duration(Duration.ofSeconds(durationSeconds))
                .calorie(calorie)
                .averagePace(Pace.ofSeconds(averagePace))
                .startAt(startAt)
                .endAt(endAt)
                .route(GeometryMapper.toDomain(route))
                .location(location)
                .emoji(emoji)
                .build();
    }
}
