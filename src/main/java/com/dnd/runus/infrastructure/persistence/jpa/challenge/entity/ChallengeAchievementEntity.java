package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.achievement.ChallengePercentageValues;
import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
    @ManyToOne(fetch = LAZY)
    private MemberEntity member;

    @NotNull
    @OneToOne(fetch = LAZY)
    private RunningRecordEntity runningRecord;

    @NotNull
    private Long challengeId;

    @NotNull
    private Boolean successStatus;

    @NotNull
    private Boolean hasPercentage;

    private Integer startValue;

    private Integer endValue;

    private Integer achievementValue;

    private Integer percentage;

    public static ChallengeAchievementEntity from(ChallengeAchievement challengeAchievement) {
        ChallengeAchievementEntityBuilder builder = ChallengeAchievementEntity.builder()
                .member(MemberEntity.from(challengeAchievement.member()))
                .runningRecord(RunningRecordEntity.from(challengeAchievement.runningRecord()))
                .challengeId(challengeAchievement.challengeId())
                .successStatus(challengeAchievement.record().successStatus())
                .hasPercentage(challengeAchievement.record().hasPercentage());

        if (challengeAchievement.record().hasPercentage()) {
            builder.startValue(challengeAchievement.record().percentageValues().startValue())
                    .endValue(challengeAchievement.record().percentageValues().endValue())
                    .achievementValue(
                            challengeAchievement.record().percentageValues().myValue())
                    .percentage(challengeAchievement.record().percentageValues().percentage());
        }

        return builder.build();
    }

    public ChallengeAchievement toDomain() {
        ChallengePercentageValues percentageValues = null;
        if (hasPercentage) {
            percentageValues = new ChallengePercentageValues(achievementValue, startValue, endValue, percentage);
        }

        ChallengeAchievementRecord record =
                new ChallengeAchievementRecord(successStatus, hasPercentage, percentageValues);

        return ChallengeAchievement.builder()
                .member(member.toDomain())
                .runningRecord(runningRecord.toDomain())
                .challengeId(challengeId)
                .record(record)
                .build();
    }
}
