package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.running.RunningRecord;

import java.util.List;
import java.util.Optional;

public interface ChallengeAchievementRepository {

    ChallengeAchievement save(ChallengeAchievement challengeAchievement);

    List<Long> findIdsByRunningRecords(List<RunningRecord> runningRecords);

    Optional<ChallengeAchievement.Status> findByRunningRecordId(long runningRecordId);

    void deleteByIds(List<Long> ids);
}
