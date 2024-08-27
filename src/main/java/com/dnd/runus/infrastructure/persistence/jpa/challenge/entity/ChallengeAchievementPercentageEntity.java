package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.PercentageValues;
import com.dnd.runus.domain.common.BaseTimeEntity;
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
@Entity(name = "challenge_achievement_percentage")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ChallengeAchievementPercentageEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = LAZY)
    private ChallengeAchievementEntity challengeAchievement;

    @NotNull
    private Integer startValue;

    @NotNull
    private Integer endValue;

    @NotNull
    private Integer achievementValue;

    public static ChallengeAchievementPercentageEntity from(
            PercentageValues percentageValues, ChallengeAchievement challengeAchievement) {
        return ChallengeAchievementPercentageEntity.builder()
                .challengeAchievement(ChallengeAchievementEntity.from(challengeAchievement))
                .startValue(percentageValues.startValue())
                .endValue(percentageValues.endValue())
                .achievementValue(percentageValues.achievementValue())
                .build();
    }

    public PercentageValues toDomain() {
        return new PercentageValues(achievementValue, startValue, endValue);
    }
}
