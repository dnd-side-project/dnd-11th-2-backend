package com.dnd.runus.domain.challenge;

import com.dnd.runus.domain.running.RunningRecord;
import lombok.RequiredArgsConstructor;

/**
 * GoalMetricType은 오늘의 챌린지의 목표 타겟에 대한 챌린지 타입을 나타냅니다.
 * GoalMetricType은 아래와 같은 타입을 나타냅니다.
 * <p> {@code DISTANCE} : 챌린지 목표 타겟이 거리인 타입
 * <p> {@code TIME} : 챌린지 목표 타겟이 시간인 타입
 * <p> {@code PACE} : 챌린지 목표 타겟이 페이스인 타입
 *
 * <p>챌린지 타입에 따라 챌린지 결과 기록을 다르게 계산합니다.
 */
@RequiredArgsConstructor
public enum GoalMetricType {
    DISTANCE,
    TIME,
    PACE,
    ;

    public boolean hasPercentage() {
        return this != PACE;
    }

    public int getActualValue(RunningRecord record) {
        return switch (this) {
            case TIME -> (int) record.duration().toSeconds();
            case DISTANCE -> record.distanceMeter();
            case PACE -> record.averagePace().toSeconds();
        };
    }
}
