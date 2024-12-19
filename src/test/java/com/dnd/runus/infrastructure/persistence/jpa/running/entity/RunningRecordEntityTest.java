package com.dnd.runus.infrastructure.persistence.jpa.running.entity;

import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunningRecordEntityTest {
    private RunningRecord runningRecord;

    @BeforeEach
    void setUp() {
        runningRecord = RunningRecord.builder()
                .runningId(1L)
                .distanceMeter(500)
                .member(new Member(1L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now()))
                .duration(Duration.ofSeconds(100))
                .calorie(1.0)
                .averagePace(Pace.ofSeconds(100))
                .startAt(ZonedDateTime.now())
                .endAt(ZonedDateTime.now())
                .route(List.of(new CoordinatePoint(128.0, 36.0), new CoordinatePoint(128.0, 37.0)))
                .startLocation("start location")
                .endLocation("end location")
                .emoji(RunningEmoji.VERY_GOOD)
                .build();
    }

    @Test
    @DisplayName("올바른 RunningRecord가 주어질 때, RunningRecordEntity.from() 메서드는 성공한다.")
    void from() {
        RunningRecordEntity runningRecordEntity = RunningRecordEntity.from(runningRecord);
        assertEquals(runningRecord.runningId(), runningRecordEntity.getId());
    }

    @Test
    @DisplayName("올바른 RunningRecordEntity가 주어질 때, RunningRecordEntity.toDomain() 메서드는 성공한다.")
    void toDomain() {
        RunningRecordEntity runningRecordEntity = RunningRecordEntity.from(runningRecord);
        RunningRecord runningRecord = runningRecordEntity.toDomain();
        assertEquals(runningRecordEntity.getId(), runningRecord.runningId());
    }
}
