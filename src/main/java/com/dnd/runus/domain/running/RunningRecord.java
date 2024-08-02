package com.dnd.runus.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.member.Member;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record RunningRecord(
        long runningId,
        Member member,
        int distanceMeter,
        int durationSeconds,
        double calorie,
        double averagePace,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        List<Coordinate> route,
        String location,
        RunningEmoji emoji) {}
