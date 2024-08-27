package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "challenge_achievement")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ChallengeAchievementEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = LAZY)
    private RunningRecordEntity runningRecord;

    @NotNull
    private Long challengeId;

    @NotNull
    private Boolean successStatus;

    public static ChallengeAchievementEntity from(ChallengeAchievement challengeAchievement) {
        return ChallengeAchievementEntity.builder()
                .runningRecord(RunningRecordEntity.from(challengeAchievement.runningRecord()))
                .challengeId(challengeAchievement.challengeId())
                .successStatus(challengeAchievement.isSuccess())
                .build();
    }

    public ChallengeAchievement toDomain() {
        return new ChallengeAchievement(id, challengeId, runningRecord.toDomain(), successStatus);
    }
}
