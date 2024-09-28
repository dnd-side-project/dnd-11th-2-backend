package com.dnd.runus.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.RunningEmoji;
import lombok.Builder;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record RunningRecord(
        long runningId,
        Member member,
        int distanceMeter,
        Duration duration,
        double calorie,
        Pace averagePace,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        List<Coordinate> route,
        String startLocation,
        String endLocation,
        RunningEmoji emoji) {}
