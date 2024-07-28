package com.dnd.runus.domain.runing.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.entity.Member;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class RunningRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Member member;

    @NotNull
    private Double distance;

    @NotNull
    private Integer runningTime;

    @NotNull
    private Double calorie;

    @NotNull
    private Double averagePace;

    @NotNull
    private Instant startAt;

    @NotNull
    private Instant endAt;

    @Nullable
    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString route;

    @NotNull
    private String location;

    @NotNull
    private Long emojiId;

    @Column(length = 500)
    private String goalDescription;

    @Builder
    private RunningRecord(
            Member member,
            Double distance,
            Integer runningTime,
            Double calorie,
            Double averagePace,
            Instant startAt,
            Instant endAt,
            LineString route,
            String location,
            Long emojiId,
            String goalDescription) {
        this.member = member;
        this.distance = distance;
        this.runningTime = runningTime;
        this.calorie = calorie;
        this.averagePace = averagePace;
        this.startAt = startAt;
        this.endAt = endAt;
        this.route = route;
        this.location = location;
        this.emojiId = emojiId;
        this.goalDescription = goalDescription;
    }
}
