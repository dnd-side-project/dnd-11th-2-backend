package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChallengeAchievementRepositoryImpl implements ChallengeAchievementRepository {

    private final JpaChallengeAchievementRepository jpaChallengeAchievementRepository;

    @Override
    public ChallengeAchievement save(ChallengeAchievement challengeAchievement) {
        return jpaChallengeAchievementRepository
                .save(ChallengeAchievementEntity.from(challengeAchievement))
                .toDomain(challengeAchievement.challenge());
    }

    @Override
    public List<Long> findIdsByRunningRecords(List<RunningRecord> runningRecords) {
        return jpaChallengeAchievementRepository
                .findAllByRunningRecordIdIn(
                        runningRecords.stream().map(RunningRecord::runningId).toList())
                .stream()
                .map(ChallengeAchievementEntity::getId)
                .toList();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        jpaChallengeAchievementRepository.deleteAllById(ids);
    }
}
