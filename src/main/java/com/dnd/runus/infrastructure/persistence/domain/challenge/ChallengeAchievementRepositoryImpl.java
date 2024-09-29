package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.infrastructure.persistence.jooq.challenge.JooqChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.JpaChallengeAchievementRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeAchievementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeAchievementRepositoryImpl implements ChallengeAchievementRepository {

    private final JpaChallengeAchievementRepository jpaChallengeAchievementRepository;
    private final JooqChallengeAchievementRepository jooqChallengeAchievementRepository;

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
    public Optional<ChallengeAchievement.Status> findByRunningRecordId(long runningRecordId) {
        return Optional.ofNullable(jooqChallengeAchievementRepository.findStatusByRunningRecordId(runningRecordId));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        jpaChallengeAchievementRepository.deleteAllById(ids);
    }
}
