package com.dnd.runus.domain.running;

import java.util.Optional;

public interface RunningRecordRepository {
    Optional<RunningRecord> findById(long runningRecordId);

    RunningRecord save(RunningRecord runningRecord);

    void deleteByMemberId(long memberId);
}
