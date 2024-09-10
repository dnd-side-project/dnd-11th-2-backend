package com.dnd.runus.domain.challenge.achievement;

import com.dnd.runus.domain.running.RunningRecord;

import java.util.List;

public interface ChallengeAchievementRepository {

    ChallengeAchievement save(ChallengeAchievement challengeAchievement);

    List<Long> findIdsByRunningRecords(List<RunningRecord> runningRecords);

    void deleteByIds(List<Long> ids);
}
