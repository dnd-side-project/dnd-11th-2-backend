package com.dnd.runus.application.running.event;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;

import java.time.Duration;

public record RunningRecordAddedEvent(
        Member member, RunningRecord runningRecord, int totalDistanceMeter, Duration totalDuration) {
    public int totalRunningSeconds() {
        return (int) totalDuration.getSeconds();
    }
}
