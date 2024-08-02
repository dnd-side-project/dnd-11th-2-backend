package com.dnd.runus.infrastructure.persistence.jpa.running.entity;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningEmoji;
import com.dnd.runus.domain.running.RunningRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunningRecordEntityTest {
    private RunningRecord runningRecord;

    @BeforeEach
    void setUp() {
        runningRecord = RunningRecord.builder()
                .runningId(1L)
                .member(Member.builder().memberId(1L).build())
                .distanceMeter(500)
                .durationSeconds(1)
                .calorie(1.0)
                .averagePace(1.0)
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now())
                .route(List.of(new Coordinate(128.0, 36.0), new Coordinate(128.0, 37.0)))
                .location("location 1")
                .emoji(new RunningEmoji(1L, "https://emoji1.com"))
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
