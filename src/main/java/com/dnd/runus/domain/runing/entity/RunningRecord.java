package com.dnd.runus.domain.runing.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.Instant;
import java.time.LocalDateTime;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class RunningRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Member member;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Double calorie;

    @Column(nullable = false)
    private Double averagePace;

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    @Column(nullable = false)
    private LocalDateTime RunDate;

    @Column(nullable = false, columnDefinition = "geometry(LineString,4326)")
    private LineString route;

    @Column(nullable = false)
    private String location;

    private Long emojiId;

    @Column(length = 500)
    private String goalDescription;

    @Builder
    private RunningRecord(
            Member member,
            String duration,
            Double distance,
            Double calorie,
            Double averagePace,
            Instant startAt,
            Instant endAt,
            LocalDateTime RunDate,
            LineString route,
            String location,
            Long emojiId,
            String goalDescription) {
        this.member = member;
        this.duration = duration;
        this.distance = distance;
        this.calorie = calorie;
        this.averagePace = averagePace;
        this.startAt = startAt;
        this.endAt = endAt;
        this.RunDate = RunDate;
        this.route = route;
        this.location = location;
        this.emojiId = emojiId;
        this.goalDescription = goalDescription;
    }
}
