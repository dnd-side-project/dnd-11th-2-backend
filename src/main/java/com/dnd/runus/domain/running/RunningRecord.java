package com.dnd.runus.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.RunningEmoji;
import lombok.Builder;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record RunningRecord(
        long runningId,
        Member member,
        int distanceMeter,
        Duration duration,
        double calorie,
        Pace averagePace,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        List<Coordinate> route,
        String location,
        RunningEmoji emoji) {}
